BITS 64

; write(1, 0x400000, 112)
mov eax, 1
mov edi, 1
mov esi, 0x400000
mov edx, 112
syscall

; exit(0)
mov eax, 60
xor edi, edi
syscall
