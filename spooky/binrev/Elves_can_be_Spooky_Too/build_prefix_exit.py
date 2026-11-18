
import struct
from pathlib import Path

SIZE = 112

P_OFFSET = 0xC0
P_VADDR = 0x0000709090C000C0

MAP_BASE = P_VADDR - P_OFFSET
ENTRY = MAP_BASE + 0x47

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
    2,          # ET_EXEC
    62,         # EM_X86_64
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

# Probe instructions embedded in p_vaddr and p_paddr
paddr_code = bytes.fromhex(
    "c0 6a 3c 58 31 ff 0f 05"
)

phdr = struct.pack(
    "<IIQQQQQQ",
    1,              # PT_LOAD
    5,              # PF_R | PF_X
    P_OFFSET,
    P_VADDR,
    int.from_bytes(paddr_code, "little"),
    1,              # p_filesz
    1,              # p_memsz
    1               # p_align
)

elf[0x38:0x38 + 56] = phdr

Path("prefix_exit").write_bytes(elf)

print(f"Size:      {SIZE}")
print(f"Map base:  {MAP_BASE:#x}")
print(f"Entry:     {ENTRY:#x}")
