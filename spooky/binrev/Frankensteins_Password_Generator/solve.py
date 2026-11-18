
#!/usr/bin/env python3
from pathlib import Path
import subprocess

CHECK_TABLE = bytes.fromhex(
    "2a 24 2a 29 43 37 45 50 "
    "5c 5d 65 72 74 6f"
)

KEY_TABLE = bytes.fromhex(
    "08 79 70 5b 47 57 b1 b4 "
    "91 93 ad ee f1 df db d6 "
    "1a 23 16 3b 1d 6a 77 44 "
    "51 5e ab b8 9a"
)


def recover_activation():
    phrase = bytearray()

    for i, target in enumerate(CHECK_TABLE):
        value = (target - (7 * i) - 0x0B) & 0xFF
        value ^= 0x5D
        phrase.append(value)

    return bytes(phrase)


def recover_flag(phrase):
    flag = bytearray()

    for i, key in enumerate(KEY_TABLE):
        value = (13 * i + 0x39) & 0xFF
        value ^= key
        value ^= phrase[i % len(phrase)]
        flag.append(value)

    return bytes(flag)


def verify_binary(phrase):
    binary = Path(__file__).resolve().parent / "frankenstein"

    if not binary.is_file():
        raise FileNotFoundError(f"Binary not found: {binary}")

    result = subprocess.run(
        [str(binary)],
        input=phrase + b"\n",
        stdout=subprocess.PIPE,
        stderr=subprocess.STDOUT,
        cwd=binary.parent,
        check=False,
    )

    print("\n[+] Local binary output:")
    print(result.stdout.decode(errors="replace"))

    return result.returncode


def main():
    phrase = recover_activation()
    flag = recover_flag(phrase)

    print(f"[+] Activation phrase : {phrase.decode()}")
    print(f"[+] Recovered flag    : {flag.decode()}")

    if not flag.startswith(b"spooky{") or not flag.endswith(b"}"):
        raise ValueError("Recovered output does not match flag format")

    print("\n[+] Running local verification...")
    verify_binary(phrase)


if __name__ == "__main__":
    main()
