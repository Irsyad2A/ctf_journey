#!/usr/bin/env python3

from pathlib import Path
import sys
from pwn import context, remote

context.log_level = "info"

HOST = "3.129.162.36"
PORT = 31903
MAX_SIZE = 112

def main():
    filename = sys.argv[1] if len(sys.argv) > 1 else "selfwrite112"
    payload = Path(filename).read_bytes()

    if not payload or len(payload) > MAX_SIZE:
        raise ValueError(f"Invalid payload size: {len(payload)}")

    print(f"[+] File: {filename}")
    print(f"[+] Size: {len(payload)} bytes")

    io = remote(HOST, PORT)

    try:
        banner = io.recvrepeat(0.5)

        print("\n[BANNER]")
        print(banner.decode(errors="replace"))

        # Protocol: SIZE <n>\n
        io.sendline(f"SIZE {len(payload)}".encode())

        # Send exactly n raw bytes
        io.send(payload)

        response = io.recvall(timeout=5)

        print("\n[SERVER RESPONSE]")
        print(response.decode(errors="replace"))

    finally:
        io.close()


if __name__ == "__main__":
    main()
