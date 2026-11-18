
import struct

SIZE = 112
BASE = 0x400000
ENTRY = BASE + 0x50

elf = bytearray(SIZE)

elf[:16] = b'\x7fELF' + bytes([2, 1, 1]) + bytes(9)

struct.pack_into(
    '<HHIQQQI',
    elf,
    0x10,
    2,          # ET_EXEC
    62,         # EM_X86_64
    1,          # Version
    ENTRY,      # Entry point
    0x38,       # Program Header offset
    0,          # Section Header offset
    0           # Flags
)

struct.pack_into('<HH', elf, 0x34, 64, 56)

phdr = bytearray(56)

struct.pack_into(
    '<IIQQ',
    phdr,
    0,
    1,          # PT_LOAD
    5,          # PF_R | PF_X
    0,          # p_offset
    BASE        # p_vaddr
)

phdr[24:32] = bytes.fromhex(
    '31 c0 b0 3c 31 ff 0f 05'
)

struct.pack_into(
    '<QQQ',
    phdr,
    32,
    SIZE,       # p_filesz
    SIZE,       # p_memsz
    1           # p_align
)

elf[0x38:0x38 + 56] = phdr

with open('exit112', 'wb') as f:
    f.write(elf)

print(f'Generated ELF: {len(elf)} bytes')
