#!/usr/bin/env python3
import re
import sys
from pathlib import Path
from collections import defaultdict

SOH = 0x01
EOT = 0x04
PACKET_SIZE = 132
DATA_SIZE = 128

HEADER = re.compile(
    r"^\s*([<>])\s+.*?\blength=(\d+)\s+from=(\d+)\s+to=(\d+)"
)


def parse_hex_line(line):
    line = line.strip()
    if not line:
        return b""

    # Buang kolom ASCII di sebelah kanan.
    hex_part = re.split(r"\s{2,}", line, maxsplit=1)[0]
    tokens = hex_part.split()

    if not tokens or not all(
        re.fullmatch(r"[0-9a-fA-F]{2}", token)
        for token in tokens
    ):
        return b""

    return bytes(int(token, 16) for token in tokens)


def load_streams(filename):
    streams = defaultdict(bytearray)
    records = []
    current = None

    def finish_record():
        nonlocal current

        if current is None:
            return

        data = bytes(current["data"])
        expected = current["length"]

        if len(data) != expected:
            records.append(
                f"WARNING: {current['direction']} "
                f"expected={expected}, got={len(data)}"
            )

        streams[current["direction"]].extend(data)
        current = None

    with open(filename, "r", errors="replace") as f:
        for line in f:
            match = HEADER.match(line)

            if match:
                finish_record()

                direction, length, start, end = match.groups()
                current = {
                    "direction": direction,
                    "length": int(length),
                    "start": int(start),
                    "end": int(end),
                    "data": bytearray(),
                }
                continue

            if current is not None:
                current["data"].extend(parse_hex_line(line))

    finish_record()
    return streams, records


def extract_transfers(stream):
    transfers = []
    current = []
    i = 0

    while i < len(stream):
        if stream[i] == SOH and i + PACKET_SIZE <= len(stream):
            block = stream[i + 1]
            complement = stream[i + 2]

            data = stream[i + 3:i + 131]
            checksum = stream[i + 131]

            valid_header = (block ^ complement) == 0xFF
            valid_checksum = (sum(data) & 0xFF) == checksum

            if valid_header and valid_checksum:
                packet = (block, bytes(data))

                # Hilangkan retransmisi identik.
                if not (
                    current
                    and current[-1] == packet
                ):
                    current.append(packet)

                i += PACKET_SIZE
                continue

        if stream[i] == EOT:
            if current:
                transfers.append(current)
                current = []

        i += 1

    if current:
        transfers.append(current)

    return transfers


def describe(data):
    if data.startswith(b"\xff\xd8\xff"):
        return "JPEG image"
    if data.startswith(b"\x89PNG\r\n\x1a\n"):
        return "PNG image"
    if data.startswith(b"GIF87a") or data.startswith(b"GIF89a"):
        return "GIF image"
    if data.startswith(b"PK\x03\x04"):
        return "ZIP archive"
    if data.startswith(b"%PDF"):
        return "PDF document"

    printable = sum(
        32 <= b <= 126 or b in (9, 10, 13)
        for b in data
    )

    if data and printable / len(data) > 0.85:
        return "Text"

    return "Unknown binary"


def main():
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} serial-capture.log")
        sys.exit(1)

    filename = sys.argv[1]
    outdir = Path("xeno_out")
    outdir.mkdir(exist_ok=True)

    streams, warnings = load_streams(filename)

    for warning in warnings:
        print(warning)

    total = 0

    for direction, stream in streams.items():
        transfers = extract_transfers(stream)

        print(
            f"\n[Direction {direction}] "
            f"Stream size: {len(stream)} bytes"
        )
        print(f"Valid transfers: {len(transfers)}")

        for index, packets in enumerate(transfers, 1):
            payload = b"".join(data for _, data in packets)

            # Simpan versi asli dan versi tanpa padding XMODEM.
            raw_path = outdir / f"transfer_{direction}_{index}.raw"
            bin_path = outdir / f"transfer_{direction}_{index}.bin"

            raw_path.write_bytes(payload)

            cleaned = payload.rstrip(b"\x1a")
            bin_path.write_bytes(cleaned)

            blocks = [number for number, _ in packets]
            gaps = []

            for previous, current in zip(blocks, blocks[1:]):
                expected = (previous + 1) & 0xFF
                if current != expected:
                    gaps.append((previous, current))

            print(f"\nTransfer #{index}")
            print(f"  Blocks     : {len(packets)}")
            print(f"  First block: {blocks[0]:02x}")
            print(f"  Last block : {blocks[-1]:02x}")
            print(f"  Raw size   : {len(payload)}")
            print(f"  Clean size : {len(cleaned)}")
            print(f"  Type       : {describe(cleaned)}")
            print(f"  Sequence gaps: {gaps[:10]}")
            print(f"  Output     : {bin_path}")

            if describe(cleaned) == "Text":
                print("\n--- Text preview ---")
                print(cleaned[:500].decode("utf-8", errors="replace"))
                print("--- End preview ---")

            total += 1

    print(f"\nTotal extracted transfers: {total}")


if __name__ == "__main__":
    main()
