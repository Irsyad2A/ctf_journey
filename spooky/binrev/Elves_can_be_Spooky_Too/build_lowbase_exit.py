
import struct
from pathlib import Path

BASE = 0x4E000
P_OFFSET = 0xB20
P_VADDR = BASE + P_OFFSET
ENTRY = BASE + 0x49

SIZE = 112

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

# Instructions in p_paddr:
# add al, al
# push 60
# pop rax
# xor edi, edi
# syscall

paddr_code = bytes.fromhex(
    "c0 6a 3c 58 31 ff 0f 05"
)

phdr = struct.pack(
    "<IIQQQQQQ",
    1,                  # PT_LOAD
    5,                  # PF_R | PF_X
    P_OFFSET,
    P_VADDR,
    int.from_bytes(paddr_code, "little"),
    1,                  # p_filesz
    1,                  # p_memsz
    1                   # p_align
)

elf[0x38:0x38 + 56] = phdr

Path("lowbase_exit").write_bytes(elf)

print("Generated lowbase_exit")
print(f"Size: {len(elf)}")
print(f"Base: {BASE:#x}")
print(f"Entry: {ENTRY:#x}")

