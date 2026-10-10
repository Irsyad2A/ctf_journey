
from pathlib import Path

enc = Path("enc").read_text(encoding="utf-8")

decoded = []

for c in enc:
    value = ord(c)

    first = chr(value >> 8)
    second = chr(value & 0xff)

    decoded.append(first)
    decoded.append(second)

flag = "".join(decoded)
print(flag)
