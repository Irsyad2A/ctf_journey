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

def main():
    with open('capture.lfc', 'rb') as f:
        data = f.read()

    total_records = read_u32(data, 0x08)
    start_id = read_u32(data, 0x0C)
    xor_key = read_u32(data, 0x18)

    offset = 0x20
    records = {}
    
    for _ in range(total_records):
        rec_id = read_u32(data, offset)
        next_id = read_u32(data, offset+4)
        size = read_u32(data, offset+8)
        payload = data[offset+0x10 : offset+0x10+size]
        records[rec_id] = {'next_id': next_id, 'data': payload}
        offset += 0x10 + size

    current_id = start_id
    step = 0
    
    while current_id in records and step < 5:
        rec = records[current_id]
        
        state1 = hash32(current_id ^ xor_key)
        dec1 = bytearray()
        for b in rec['data']:
            state1 = xorshift(state1)
            dec1.append((state1 & 0xff) ^ b)

        print(f"Record ID: {current_id:08x}")
        print(f"  Dec1 (First Decryption): {dec1.hex()}")
        print()

        current_id = rec['next_id']
        step += 1

if __name__ == '__main__':
    main()