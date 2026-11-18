
import struct
from pathlib import Path

data = bytearray(Path("large_exit_probe").read_bytes())

# ELF64 Program Header:
# p_filesz berada di offset 0x58
# p_memsz  berada di offset 0x60

struct.pack_into("<Q", data, 0x58, 0x1000)
struct.pack_into("<Q", data, 0x60, 0x1000)

Path("small_segment_probe").write_bytes(data)

print("Generated: small_segment_probe")
print("p_filesz = 0x1000")
print("p_memsz  = 0x1000")
print("Size     =", len(data))
