
from pathlib import Path
import struct

data = bytearray(Path("exit112").read_bytes())

# Ubah e_entry menjadi alamat awal ELF Header
struct.pack_into("<Q", data, 0x18, 0x400000)

Path("entry0").write_bytes(data)

print("Generated entry0")
print(f"Size: {len(data)} bytes")
print("Entry point: 0x400000")
