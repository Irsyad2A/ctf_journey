
import struct
from pathlib import Path

SIZE = 112
BASE = 0x4E000

P_OFFSET = 0xB20
P_VADDR = BASE + P_OFFSET
ENTRY = BASE + 0x49

# Byte yang sekaligus membentuk instruction stream
paddr = bytes.fromhex(
    "c2 b0 01 ff c7 b2 70 be"
)

# Nilainya besar, tetapi byte-nya digunakan sebagai instruksi.
file_mem_size = bytes.fromhex(
    "00 e0 04 00 eb 0a 00 00"
)

# Diagnostic marker: UD2 + NOP padding
align_code = bytes.fromhex(
    "0f 0b 90 90 90 90 90 90"
)

elf = bytearray(SIZE)

ident = b"\x7fELF" + bytes([2, 1, 1, 0]) + bytes(8)

struct.pack_into(
    "<16sHHIQQQIHHHHHH",
    elf,
    0,
    ident,
    2,          # ET_EXEC
    62,         # EM_X86_64
    1,
    ENTRY,
    0x38,       # e_phoff
    0,
    0,
    64,
    56,
    1,
    64,
    0,
    0
)

phdr = struct.pack(
    "<IIQQQQQQ",
    1,                          # PT_LOAD
    5,                          # PF_R | PF_X
    P_OFFSET,
    P_VADDR,
    int.from_bytes(paddr, "little"),
    int.from_bytes(file_mem_size, "little"),
    int.from_bytes(file_mem_size, "little"),
    int.from_bytes(align_code, "little")
)

elf[0x38:0x38 + 56] = phdr

Path("setup112_probe").write_bytes(elf)

print(f"Size:  {len(elf)}")
print(f"Base:  {BASE:#x}")
print(f"Entry: {ENTRY:#x}")
