import struct
from pathlib import Path

SIZE = 112
P_OFFSET = 0xC0
P_VADDR = 0x4000C0          # <4GB, supaya "mov esi, imm32" bisa dipakai utuh
MAP_BASE = P_VADDR - P_OFFSET   # 0x400000

ENTRY_FILE_OFF = 0x28       # mulai di e_shoff (bebas, tidak divalidasi kernel)
JMP_TARGET_FILE_OFF = 0x50  # p_paddr (bebas total, tidak divalidasi)
ENTRY = MAP_BASE + ENTRY_FILE_OFF

elf = bytearray(SIZE)
ident = b"\x7fELF" + bytes([2, 1, 1, 0]) + bytes(8)

struct.pack_into(
    "<16sHHIQQQIHHHHHH",
    elf, 0,
    ident,
    2, 62, 1,
    ENTRY,      # e_entry
    0x38,       # e_phoff (overlap trick, sama seperti sebelumnya)
    0, 0, 64, 56, 1, 64, 0, 0
)

# ---- Region A: 0x28 - 0x35 (14 byte bebas: e_shoff+e_flags+e_ehsize) ----
region_a = bytearray()
region_a += bytes.fromhex("b270")      # mov dl, 112      (rdx = filesize)
region_a += bytes.fromhex("ffc7")      # inc edi          (rdi = 1)
region_a += bytes.fromhex("ffc0")      # inc eax          (rax = 1, sys_write)
region_a += bytes.fromhex("be") + struct.pack("<I", MAP_BASE)  # mov esi, MAP_BASE
assert len(region_a) == 11, len(region_a)

jmp_pos_in_region_a = len(region_a)
rel8 = JMP_TARGET_FILE_OFF - (ENTRY_FILE_OFF + jmp_pos_in_region_a + 2)
assert -128 <= rel8 <= 127, rel8
region_a += bytes([0xEB, rel8 & 0xFF])  # jmp rel8 -> p_paddr
print("Region A len:", len(region_a), "rel8:", rel8)
assert len(region_a) <= 14

elf[ENTRY_FILE_OFF:ENTRY_FILE_OFF + len(region_a)] = region_a
# sisa byte di region A (kalau ada) biarkan 0 -- tidak pernah dieksekusi (sudah jmp duluan)

# ---- Program header: p_type,p_flags,p_offset,p_vaddr (0x38-0x4F) ----
phdr_head = struct.pack("<IIQQ", 1, 5, P_OFFSET, P_VADDR)
assert len(phdr_head) == 24
elf[0x38:0x38 + len(phdr_head)] = phdr_head

# ---- Region B: 0x50 - 0x57 (p_paddr, 8 byte, bebas total) ----
region_b = bytearray()
region_b += bytes.fromhex("0f05")      # syscall           -> write(1, MAP_BASE, 112)
region_b += bytes.fromhex("ffcf")      # dec edi            (rdi = 0)
region_b += bytes.fromhex("b03c")      # mov al, 0x3c       (rax = 60, exit)
region_b += bytes.fromhex("0f05")      # syscall           -> exit(0)
assert len(region_b) == 8, len(region_b)
elf[0x50:0x50 + len(region_b)] = region_b

# ---- p_filesz / p_memsz / p_align: nilai AMAN eksplisit (bukan kode) ----
struct.pack_into("<Q", elf, 0x58, 1)   # p_filesz = 1
struct.pack_into("<Q", elf, 0x60, 1)   # p_memsz  = 1
struct.pack_into("<Q", elf, 0x68, 1)   # p_align  = 1

Path("quine_try2").write_bytes(elf)
print(f"Size: {len(elf)}  Entry: {ENTRY:#x}  MAP_BASE: {MAP_BASE:#x}")
