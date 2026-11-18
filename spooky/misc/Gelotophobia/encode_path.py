
import base64
import sys

def encode_path(value):
    raw = value.encode("utf-16-be")

    bits = "".join(f"{byte:08b}" for byte in raw)
    case_data = "".join(
        "E" if bit == "0" else "e"
        for bit in bits
    )

    return base64.b64encode(case_data.encode()).decode()


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <path>")
        sys.exit(1)

    print(encode_path(sys.argv[1]))
