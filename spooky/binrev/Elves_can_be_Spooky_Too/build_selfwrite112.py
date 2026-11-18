
from pathlib import Path

source = Path("setup112_probe")
output = Path("selfwrite112")

data = bytearray(source.read_bytes())

assert len(data) == 112
assert data[0x68:0x70] == bytes.fromhex(
    "0f 0b 90 90 90 90 90 90"
)

# write(1, 0x4e000, 112)
# exit(0)
data[0x68:0x70] = bytes.fromhex(
    "0f 05 b0 3c 31 ff 0f 05"
)

output.write_bytes(data)

print(f"Generated: {output}")
print(f"Size: {len(data)} bytes")
print(f"p_align bytes: {data[0x68:0x70].hex(' ')}")
