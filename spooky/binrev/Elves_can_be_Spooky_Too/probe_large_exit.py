
from pathlib import Path
import struct

data = bytearray(Path("selfwrite112").read_bytes())

# Entry point langsung ke p_align
BASE = 0x4E000
struct.pack_into("<Q", data, 0x18, BASE + 0x68)

# push 60
# pop rax
# xor edi, edi
# syscall
# nop
data[0x68:0x70] = bytes.fromhex(
    "6a 3c 58 31 ff 0f 05 90"
)

Path("large_exit_probe").write_bytes(data)

print(f"Size: {len(data)} bytes")
print(f"Entry: {BASE + 0x68:#x}")
