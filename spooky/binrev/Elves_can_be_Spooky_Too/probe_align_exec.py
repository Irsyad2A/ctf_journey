
import struct
from pathlib import Path

BASE = 0x709090C00000
data = bytearray(Path("prefix_exit").read_bytes())

# Pindahkan entry point ke offset p_align
struct.pack_into("<Q", data, 0x18, BASE + 0x68)

# Instruksi UD2 di dalam p_align
data[0x68:0x70] = bytes.fromhex(
    "0f 0b 90 90 90 90 90 90"
)

Path("align_exec_probe").write_bytes(data)

print("Entry point:", hex(BASE + 0x68))
print("Instruction: UD2")
print("Size:", len(data))
