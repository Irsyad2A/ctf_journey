from pwn import *
context.binary = elf = ELF('./entropy_patched')
p = process('./entropy_patched')

# Breakpoint di main+232 (leave)
gdb.attach(p, gdbscript='''
b *main+232
c
''')

# Kirim '0' agar strtol == 0, loop break, dan masuk ke leave
p.sendline(b'0')
p.interactive()
