import struct

def hash32(x):
    x = ((x >> 16) ^ x) * 0x7feb352d & 0xffffffff
    x = ((x >> 15) ^ x) * 0x846ca68b & 0xffffffff
    return ((x >> 16) ^ x) & 0xffffffff

def xorshift(state):
    state = (state << 13 ^ state) & 0xffffffff
    state = (state >> 17 ^ state) & 0xffffffff
    state = (state << 5 ^ state) & 0xffffffff
    return state

def read_u32(data, offset):
    return struct.unpack_from('<I', data, offset)[0]

def read_u16(data, offset):
    return struct.unpack_from('<H', data, offset)[0]

def main():
    with open('capture.lfc', 'rb') as f:
        data = f.read()

    total_records = read_u32(data, 0x08)
    start_id = read_u32(data, 0x0C)
    xor_key = read_u32(data, 0x18)

    offset = 0x20
    records = []
    
    # Parsing semua records ke dalam dictionary berdasarkan ID
    for _ in range(total_records):
        rec_id = read_u32(data, offset)
        next_id = read_u32(data, offset+4)
        size = read_u32(data, offset+8)
        checksum = read_u32(data, offset+0xC)
        payload = data[offset+0x10 : offset+0x10+size]
        
        records.append({
            'id': rec_id, 'next_id': next_id, 'size': size,
            'checksum': checksum, 'data': payload
        })
        offset += 0x10 + size

    current_id = start_id
    step = 0
    
    # Mengikuti Linked List
    while current_id != 0 and step < 15: # Batasi 15 frame pertama untuk melihat
        rec = next((r for r in records if r['id'] == current_id), None)
        if not rec:
            print(f"Record {current_id:08x} tidak ditemukan!")
            break
            
        print(f"--- Record {step} (ID: {rec['id']:08x}, Size: {rec['size']}) ---")
        data = rec['data']

        # 1. Dekripsi Pertama (dari fungsi entry)
        state1 = hash32(rec['id'] ^ xor_key)
        dec1 = bytearray()
        for b in data:
            state1 = xorshift(state1)
            dec1.append((state1 & 0xff) ^ b)

        # 2. Dekripsi Kedua (dari VM FUN_100000ed0)
        state2 = hash32(rec['id'] ^ 0x6c8e9cf5)
        dec2 = bytearray()
        for i, b in enumerate(dec1):
            op = (state2 & 0xff) ^ b
            dec2.append(op)
            # Formula update state sesuai instruksi assembly (bukan pseudocode Ghidra)
            state2 = hash32(((op ^ state2) * 0x9e37) ^ (i+1))

        # 3. Parsing Opcodes
        pos = 0
        while pos < len(dec2):
            cmd = dec2[pos]
            if cmd == 0x3d:
                print(f"  CMD 0x3d: Set Palette PRNG")
                pos += 5
            elif cmd in (0x47, 0x58):
                x = read_u16(dec2, pos+1)
                flags = dec2[pos+3]
                print(f"  CMD {cmd:02x}: Draw Tile x={x} flags={flags:02x}")
                pos += 4
            elif cmd == 0xad:
                print(f"  CMD 0xad: Commit Frame")
                pos += 5
            elif cmd == 0xf1:
                print("  CMD 0xf1: END OF STREAM")
                pos += 1
                break
            elif cmd == 0x61:
                val = read_u16(dec2, pos+1)
                print(f"  CMD 0x61: Set 0x10318 val={val}")
                pos += 3
            elif cmd == 0x0b:
                print("  CMD 0x0b: Header Data (16 bytes)")
                pos += 17
            elif cmd == 0x12:
                val = read_u16(dec2, pos+1)
                print(f"  CMD 0x12: Set 0x10314 val={val}")
                pos += 3
            elif cmd == 0x76:
                val = read_u16(dec2, pos+1)
                print(f"  CMD 0x76: XOR Palette count={val}")
                pos += 4
            elif cmd == 0x8b:
                val = read_u16(dec2, pos+1)
                print(f"  CMD 0x8b: XOR Palette Data count={val}")
                pos += 3 + (val + 1) // 2
            elif cmd == 0x9e:
                print(f"  CMD 0x9e: Palette Shift")
                pos += 2
            else:
                print(f"  CMD {cmd:02x}: Unknown at pos {pos}")
                break

        current_id = rec['next_id']
        step += 1
        print()

if __name__ == '__main__':
    main()
