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

def fnv1a(data):
    h = 0x811c9dc5
    for b in data:
        h = ((b ^ h) * 0x1000193) & 0xffffffff
    return h

def unpack_tile(framebuffer, p2, p3):
    offset = ((p3 * 0xc5 + 0x65) & 0xff) << 7 | ((p2 & 1) << 15)
    prng = hash32((p2 * 0x10204081) ^ (p3 * 0x9e3779b9) ^ 0xa53c9e17)
    raw = bytearray(128)
    for i in range(128):
        b = framebuffer[offset + i]
        prng = xorshift(prng)
        raw[i] = b ^ (prng & 0xff)

    out = bytearray(256)
    for y in range(16):
        for x in range(16):
            bit_pos = (x * 2 & 4) | (x & 1) | (y * 2 & 2)
            uVar7 = x * 8
            uVar8 = x * 4
            uVar10 = ((uVar7 & 0x40) | (y & 8) * 0x10 + (y & 4) * 8 + (y & 2) * 4 | (uVar8 & 0x10)) >> 3
            b0 = (raw[uVar10] >> bit_pos) & 1
            b1 = (raw[uVar10 + 0x20] >> bit_pos) & 1
            b2 = (raw[uVar10 + 0x40] >> bit_pos) & 1
            b3 = (raw[uVar10 + 0x60] >> bit_pos) & 1
            out[y * 16 + x] = b0 | (b1 << 1) | (b2 << 2) | (b3 << 3)
    return out

def commit_frame(state):
    p2 = state['10310']
    p3 = state['10314']
    offset = ((p3 * 0xc5 + 0x65) & 0xff) << 7 | ((p2 & 1) << 15)
    
    raw = bytearray(128)
    for y in range(16):
        for x in range(16):
            b = state['palette'][y * 16 + x]
            bit_pos = (x * 2 & 4) | (x & 1) | (y * 2 & 2)
            uVar7 = x * 8
            uVar8 = x * 4
            uVar10 = ((uVar7 & 0x40) | (y & 8) * 0x10 + (y & 4) * 8 + (y & 2) * 4 | (uVar8 & 0x10)) >> 3
            raw[uVar10] |= ((b & 1) << bit_pos) & 0xff
            raw[uVar10 + 0x20] |= (((b >> 1) & 1) << bit_pos) & 0xff
            raw[uVar10 + 0x40] |= (((b >> 2) & 1) << bit_pos) & 0xff
            raw[uVar10 + 0x60] |= (((b >> 3) & 1) << bit_pos) & 0xff

    prng = hash32((p2 * 0x10204081) ^ (p3 * 0x9e3779b9) ^ 0xa53c9e17)
    for i in range(128):
        prng = xorshift(prng)
        state['framebuffer'][offset + i] = raw[i] ^ (prng & 0xff)

    state['tile_map'][p3 + ((p2 & 1) << 8)] = 1
    state['frames_committed'] += 1

def save_ppm(filename, width, height, data):
    with open(filename, 'wb') as f:
        f.write(f"P5\n{width} {height}\n255\n".encode())
        f.write(data)

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

    state = {
        'framebuffer': bytearray(0x10000),
        'tile_map': bytearray(512),
        'palette': bytearray(256),
        '10310': 0, '10314': 0, '10318': 0, '1031c': 0, '10320': 0, '10324': 0,
        'frames_committed': 0
    }

    current_id = start_id
    step = 0
    
    while current_id in records and step < total_records:
        rec = records[current_id]
        
        state1 = hash32(current_id ^ xor_key)
        dec1 = bytearray()
        for b in rec['data']:
            state1 = xorshift(state1)
            dec1.append((state1 & 0xff) ^ b)

        pos = 0
        state2 = hash32(current_id ^ 0x6c8e9cf5)
        retries = 0
        
        while pos < len(dec1) and retries < 1024:
            op = (state2 & 0xff) ^ dec1[pos]
            state2 = hash32(((op ^ state2) * 0x9e37) ^ (pos + 1))
            
            advanced = False
            
            if op == 0x3d:
                if pos + 5 <= len(dec1) and state['1031c'] != 0 and state['10320'] == 0:
                    prng = read_u32(dec1, pos+1)
                    for i in range(256):
                        prng = xorshift(prng)
                        state['palette'][i] = prng & 0xf
                    pos += 5
                    advanced = True
                
            elif op in (0x47, 0x58):
                if pos + 4 <= len(dec1):
                    val = read_u16(dec1, pos+1)
                    flags = dec1[pos+3]
                    y_val = state['10310']
                    if state['1031c'] != 0 and state['10320'] == 0 and y_val != 0 and val < 0x100 and flags < 8 and state['tile_map'][val + ((~y_val & 1) << 8)] != 0:
                        out_pal = unpack_tile(state['framebuffer'], y_val - 1, val)
                        local_238 = bytearray(256)
                        pbVar15 = 1
                        for uVar14 in range(16):
                            uVar18 = 0xf - uVar14
                            if (flags & 2) == 0: uVar18 = uVar14
                            uVar28 = 0xe
                            for lVar16 in range(0, 16, 2):
                                uVar29 = lVar16 + 1
                                uVar30 = lVar16
                                if (flags & 1) != 0:
                                    uVar29 = uVar28
                                    uVar30 = uVar28 + 1
                                bVar26 = flags < 4
                                uVar20 = uVar18 if not bVar26 else uVar30
                                uVar23 = uVar18 if not bVar26 else uVar29
                                uVar9 = uVar30 if not bVar26 else uVar18
                                uVar22 = uVar29 if not bVar26 else uVar18
                                local_238[pbVar15 + lVar16 - 1] = out_pal[uVar20 + (uVar9 << 4)]
                                local_238[pbVar15 + lVar16] = out_pal[uVar23 + (uVar22 << 4)]
                                uVar28 -= 2
                            pbVar15 += 0x10
                            
                        if op == 0x47:
                            state['palette'] = local_238
                        else:
                            for i in range(256):
                                state['palette'][i] ^= local_238[i]
                        pos += 4
                        advanced = True
                
            elif op == 0xad:
                if pos + 5 <= len(dec1):
                    chk = read_u32(dec1, pos+1)
                    if state['10318'] == 0x100 and fnv1a(state['palette']) == chk:
                        commit_frame(state)
                        state['palette'] = bytearray(256)
                        state['1031c'] = 0
                        state['10320'] = 1
                        pos += 5
                        advanced = True
                
            elif op == 0xc3:
                if pos + 5 <= len(dec1) and state['10310'] < 3 and state['10324'] != 0 and state['1031c'] == 0:
                    state['10310'] += 1
                    if state['10310'] < 3:
                        for i in range(256):
                            state['tile_map'][((state['10310'] & 1) << 8) + i] = 0
                    pos += 5
                    advanced = True
                
            elif op == 0xf1:
                break
                
            elif op == 0x61:
                if pos + 3 <= len(dec1) and state['1031c'] != 0 and state['10320'] == 0:
                    val = read_u16(dec1, pos+1)
                    if val <= 0x100:
                        state['10318'] = val
                        pos += 3
                        advanced = True
                
            elif op == 0x0b:
                if pos + 17 <= len(dec1) and state['10324'] == 0 and state['10310'] == 0 and state['1031c'] == 0:
                    state['10324'] = 1
                    pos += 17
                    advanced = True
                
            elif op == 0x12:
                if pos + 3 <= len(dec1):
                    val = read_u16(dec1, pos+1)
                    if val <= 0xff and state['10310'] <= 2 and state['10324'] != 0 and state['1031c'] == 0 and state['tile_map'][val + ((state['10310'] & 1) << 8)] == 0:
                        state['10314'] = val
                        state['10318'] = 0
                        state['1031c'] = 1
                        state['10320'] = 0
                        state['10328'] = 0
                        state['palette'] = bytearray(256)
                        pos += 3
                        advanced = True
                
            elif op == 0x76:
                if pos + 4 <= len(dec1):
                    val = read_u16(dec1, pos+1)
                    flags = dec1[pos+3]
                    if state['1031c'] != 0 and state['10320'] == 0 and val != 0 and state['10318'] + val <= 0x100 and flags < 0x10:
                        for i in range(val):
                            state['palette'][state['10318'] + i] ^= flags
                        state['10318'] += val
                        pos += 4
                        advanced = True
                
            elif op == 0x8b:
                if pos + 3 <= len(dec1):
                    val = read_u16(dec1, pos+1)
                    needed = (val + 1) // 2
                    if state['1031c'] != 0 and state['10320'] == 0 and val != 0 and state['10318'] + val <= 0x100 and pos + 3 + needed <= len(dec1):
                        for i in range(val):
                            b = dec1[pos+3 + i//2]
                            b = (b >> (0 if i%2==0 else 4)) & 0xf
                            state['palette'][state['10318'] + i] ^= b
                        state['10318'] += val
                        pos += 3 + needed
                        advanced = True
                
            elif op == 0x9e:
                if pos + 2 <= len(dec1):
                    flags = dec1[pos+1]
                    if state['1031c'] != 0 and state['10320'] == 0 and flags < 3:
                        if flags == 2:
                            for i in range(16, 256, 16):
                                for j in range(16):
                                    state['palette'][i + j] ^= state['palette'][i - 16 + j]
                        elif flags == 1:
                            for i in range(0, 256, 16):
                                for j in range(1, 16):
                                    state['palette'][i + j] ^= state['palette'][i + j - 1]
                        pos += 2
                        advanced = True
            
            if advanced:
                retries = 0
            else:
                retries += 1

        current_id = rec['next_id']
        step += 1

    print(f"Emulation finished. Committed {state['frames_committed']} frames. Saving image...")
    save_ppm('flag.ppm', 512, 128, bytes(state['framebuffer']))
    print("Gambar disimpan sebagai 'flag.ppm'. Silakan buka file tersebut!")

if __name__ == '__main__':
    main()