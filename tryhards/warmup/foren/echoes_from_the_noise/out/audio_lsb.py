import wave

w = wave.open('recording.wav', 'rb')
frames = w.readframes(w.getnframes())
w.close()

# Mono 16-bit PCM (Little-endian)
# Setiap sampel adalah 2 byte. Kita ambil LSB dari setiap 16-bit integer.
bits = []
for i in range(0, len(frames), 2):
    # frames[i] adalah byte pertama (LSB dari 16-bit sample)
    bits.append(frames[i] & 1)

# Konversi bit ke karakter ASCII (8 bit per karakter)
chars = []
for i in range(0, len(bits) - 7, 8):
    byte = 0
    for j in range(8):
        byte = (byte << 1) | bits[i+j]
    chars.append(chr(byte) if 32 <= byte <= 126 else '.')

print("=== TEKS LSB PADA AUDIO ===")
print(''.join(chars)[:2000])
