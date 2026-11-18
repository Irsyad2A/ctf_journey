
import struct
from pathlib import Path

BASE = 0x709090C00000
VADDR = BASE + 0xC0
ENTRY = BASE + 0x4B

CODE_OFFSET = 0x70

# write(1, BASE, 145)
# exit(0)
code = bytes.fromhex(
    "b8 01 00 00 00 "
    "bf 01 00 00 00 "
    "48 8d 35 7f ff ff ff "
    "ba 91 00 00 00 "
    "0f 05 "
    "b8 3c 00 00 00 "
    "31 ff "
    "0f 05"
)

SIZE = CODE_OFFSET + len(code)

assert SIZE == 145

elf = bytearray(SIZE)

ident = (
    b"\x7fELF"
    + bytes([2, 1, 1, 0])
    + bytes(8)
)

# ELF Header
struct.pack_into(
    "<16sHHIQQQIHHHHHH",
    elf,
    0,
    ident,
    2,
    62,
    1,
    ENTRY,
    0x38,
    0,
    0,
    64,
    56,
    1,
    64,
    0,
    0
)

# p_paddr doubles as machine-code bytes
paddr_code = bytes.fromhex(
    "c0 eb 1d 00 00 00 00 00"
)

phdr = struct.pack(
    "<IIQQQQQQ",
    1,          # PT_LOAD
    5,          # R-X
    0xC0,       # p_offset
    VADDR,      # p_vaddr
    int.from_bytes(paddr_code, "little"),
    1,          # p_filesz
    1,          # p_memsz
    1           # p_align
)

elf[0x38:0x38 + 56] = phdr
elf[CODE_OFFSET:] = code

Path("prefix_write").write_bytes(elf)

print(f"Size: {SIZE}")
print(f"Entry: {ENTRY:#x}")
print(f"Code offset: {CODE_OFFSET:#x}")
