from PIL import Image

im = Image.open('photo.bmp').convert('RGB')
w, h = im.size
pix = im.load()

for channel_idx, channel_name in enumerate(['Red', 'Green', 'Blue']):
    bits = []
    for y in range(h):
        for x in range(w):
            bits.append(pix[x, y][channel_idx] & 1)

    chars = []
    for i in range(0, len(bits) - 7, 8):
        byte = 0
        for j in range(8):
            byte = (byte << 1) | bits[i+j]
        chars.append(chr(byte) if 32 <= byte <= 126 else '.')

    print(f"=== LSB HANYA {channel_name.upper()} ===")
    print(''.join(chars)[:500])
    print("\n")
