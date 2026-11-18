
import re
import base64
from pathlib import Path

source = Path("testindex2.js").read_text()

tokens = re.findall(
    r'"([A-Za-z0-9+/]+={0,2})"',
    source
)

print(f"[+] Found {len(tokens)} entries\n")

results = []

for index, token in enumerate(tokens, 1):
    try:
        # Lengkapi padding Base64 yang hilang.
        remainder = len(token) % 4

        if remainder == 1:
            raise ValueError(
                f"Invalid Base64 length: {len(token)}"
            )

        token += "=" * ((-len(token)) % 4)

        raw = base64.b64decode(token, validate=True)

        if not set(raw).issubset({ord("E"), ord("e")}):
            raise ValueError("Unexpected characters")

        bits = "".join(
            "0" if char == ord("E") else "1"
            for char in raw
        )

        if len(bits) % 8:
            raise ValueError("Invalid bit length")

        decoded_bytes = bytes(
            int(bits[i:i+8], 2)
            for i in range(0, len(bits), 8)
        )

        decoded = decoded_bytes.decode("utf-16-be")

        results.append((index, decoded))

        print(f"[{index:02}] {decoded!r}")

    except Exception as error:
        print(f"[{index:02}] ERROR: {error}")
        print(f"     Original: {token!r}")

Path("decoded.txt").write_text(
    "\n".join(value for _, value in results) + "\n"
)

print(f"\n[+] Successfully decoded: {len(results)}/{len(tokens)}")
print("[+] Saved valid entries to decoded.txt")
