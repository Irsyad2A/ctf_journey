from PIL import Image

im = Image.open('photo.bmp').convert('RGB')
w, h = im.size
pix = im.load()

bits = []
for y in range(h):
    for x in range(w):
        for color in pix[x, y]:
            bits.append(color & 1)

# Coba baca sebagai karakter ASCII (8 bit per huruf)
chars = []
for i in range(0, len(bits) - 7, 8):
    byte = 0
    for j in range(8):
        byte = (byte << 1) | bits[i+j]
    chars.append(chr(byte) if 32 <= byte <= 126 else '.')

print("=== TEKS LSB PADA GAMBAR ===")
print(''.join(chars)[:1000])

