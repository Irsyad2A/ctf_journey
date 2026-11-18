values = [
    0x99cd330, 0x804bff4, 0x80494c3, 0xffffffff,
    0xf1751a8c, 0xf1974400, 0x99cb1a0, 0xf1965d40,
    0x804a14d, 0x99cc1c0, 0x99cd310, 0x99cd330,
    0x64616361, 0x7b796d65, 0x306c5f49, 0x345f7435,
    0x6d5f6c6c, 0x306d5f79, 0x5f79336e, 0x63306639,
    0x34343861, 0x804834c, 0xf19b2fe8
]

for value in values:
    chunk = value.to_bytes(4, byteorder="little")

    if all(32 <= byte <= 126 for byte in chunk):
        print(hex(value), "->", chunk.decode("ascii"))