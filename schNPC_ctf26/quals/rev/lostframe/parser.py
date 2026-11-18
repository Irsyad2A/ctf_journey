import struct

def hash32(x):
    x &= 0xffffffff
    x = ((x >> 16) ^ x) * 0x7feb352d & 0xffffffff
    x = ((x >> 15) ^ x) * 0x846ca68b & 0xffffffff
    return ((x >> 16) ^ x) & 0xffffffff

def xorshift(state):
    state &= 0xffffffff
    state = (state << 13 ^ state) & 0xffffffff
    state = (state >> 17 ^ state) & 0xffffffff
    state = (state << 5 ^ state) & 0xffffffff
    return state & 0xffffffff

def read_u32(data, offset):
    return struct.unpack_from('<I', data, offset)[0]

def read_u16(data, offset):
    return struct.unpack_from('<H', data, offset)[0]

def parse_commands(records):
    for i, rec in enumerate(records):
        print(f"--- Record {i} (ID: {rec['id']:08x}, Size: {rec['size']}) ---")
        data = rec['data']
        pos = 0
        while pos < len(data):
            cmd = data[pos]
            if cmd == 0x3d:
                seed = read_u32(data, pos+1)
                print(f"  CMD 0x3d: Set Palette PRNG, seed={seed:08x}")
                pos += 5
            elif cmd in (0x47, 0x58):
                x = read_u16(data, pos+1)
                flags = data[pos+3]
                print(f"  CMD {cmd:02x}: Draw Tile, x={x}, flags={flags:02x}")
                pos += 4
            elif cmd == 0xad:
                chk = read_u32(data, pos+1)
                print(f"  CMD 0xad: Commit Frame, checksum={chk:08x}")
                pos += 5
            elif cmd == 0xf1:
                print("  CMD 0xf1: END")
                pos += 1
                break
            elif cmd == 0x61:
                val = read_u16(data, pos+1)
                print(f"  CMD 0x61: Set 0x10318, val={val}")
                pos += 3
            elif cmd == 0x0b:
                print("  CMD 0x0b: Set Header Data (16 bytes)")
                pos += 17
            elif cmd == 0x12:
                val = read_u16(data, pos+1)
                print(f"  CMD 0x12: Set 0x10314, val={val}")
                pos += 3
            elif cmd == 0x76:
                val = read_u16(data, pos+1)
                flags = data[pos+3]
                print(f"  CMD 0x76: XOR Palette, count={val}, flags={flags:02x}")
                pos += 4
            elif cmd == 0x8b:
                val = read_u16(data, pos+1)
                print(f"  CMD 0x8b: XOR Palette from Data, count={val}")
                pos += 3 + (val + 1) // 2
            elif cmd == 0x9e:
                flags = data[pos+1]
                print(f"  CMD 0x9e: Palette Shift, flags={flags:02x}")
                pos += 2
            else:
                print(f"  CMD {cmd:02x}: Unknown at pos {pos}")
                break
        print()

def main():
    with open('capture.lfc', 'rb') as f:
        data = f.read()

    total_records = read_u32(data, 0x08)
    start_id = read_u32(data, 0x0C)
    xor_key = read_u32(data, 0x18)

    offset = 0x20
    records = []
    
    for _ in range(total_records):
        rec_id = read_u32(data, offset)
        next_id = read_u32(data, offset+4)
        size = read_u32(data, offset+8)
        checksum = read_u32(data, offset+0xC)
        
        payload = data[offset+0x10 : offset+0x10+size]
        
        state = hash32(rec_id ^ xor_key)
        decrypted = bytearray()
        for b in payload:
            state = xorshift(state)
            decrypted.append((state & 0xff) ^ b)
            
        records.append({
            'id': rec_id,
            'next_id': next_id,
            'size': size,
            'checksum': checksum,
            'data': decrypted
        })
        offset += 0x10 + size
        if offset >= len(data):
            break

    print(f"Berhasil memparsing {len(records)} records.\n")
    # Tampilkan 10 record pertama untuk melihat strukturnya
    parse_commands(records[:10])

if __name__ == '__main__':
    main()
