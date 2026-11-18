
import struct
from pathlib import Path

BASE = 0x400000
CODE_OFFSET = 64 + 56

# write(1, 0x400000, 151)
# exit(0)
code = bytes.fromhex(
    "b8 01 00 00 00 "
    "bf 01 00 00 00 "
    "be 00 00 40 00 "
    "ba 97 00 00 00 "
    "0f 05 "
    "b8 3c 00 00 00 "
    "31 ff "
    "0f 05"
)

SIZE = CODE_OFFSET + len(code)
ENTRY = BASE + CODE_OFFSET

ident = (
    b"\x7fELF"
    + bytes([2, 1, 1, 0])
    + bytes(8)
)

# Standard ELF64 Header
ehdr = struct.pack(
    "<16sHHIQQQIHHHHHH",
    ident,
    2,          # ET_EXEC
    62,         # EM_X86_64
    1,          # Version
    ENTRY,
    64,         # e_phoff
    0,          # e_shoff
    0,          # e_flags
    64,         # e_ehsize
    56,         # e_phentsize
    1,          # e_phnum
    64,         # e_shentsize
    0,          # e_shnum
    0           # e_shstrndx
)

# Standard ELF64 Program Header
phdr = struct.pack(
    "<IIQQQQQQ",
    1,          # PT_LOAD
    5,          # PF_R | PF_X
    0,          # p_offset
    BASE,       # p_vaddr
    0,          # p_paddr
    SIZE,       # p_filesz
    SIZE,       # p_memsz
    0x1000      # p_align
)

elf = ehdr + phdr + code

assert len(elf) == SIZE

Path("selfwrite151").write_bytes(elf)

print(f"Generated: {SIZE} bytes")
print(f"Entry:     {ENTRY:#x}")
print(f"Code:      {len(code)} bytes")
