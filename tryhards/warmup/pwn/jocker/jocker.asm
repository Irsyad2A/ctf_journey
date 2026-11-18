
rootfs/root/jocker.ko:     file format elf64-x86-64


Disassembly of section .text:

0000000000000000 <jocker_ioctl>:
   0:	f3 0f 1e fa          	endbr64
   4:	55                   	push   rbp
   5:	48 c7 c7 00 00 00 00 	mov    rdi,0x0
   c:	48 89 d5             	mov    rbp,rdx
   f:	53                   	push   rbx
  10:	89 f3                	mov    ebx,esi
  12:	48 83 ec 10          	sub    rsp,0x10
  16:	48 c7 04 24 00 00 00 	mov    QWORD PTR [rsp],0x0
  1d:	00 
  1e:	48 c7 44 24 08 00 00 	mov    QWORD PTR [rsp+0x8],0x0
  25:	00 00 
  27:	e8 00 00 00 00       	call   2c <jocker_ioctl+0x2c>
  2c:	81 fb 02 6a 00 00    	cmp    ebx,0x6a02
  32:	0f 84 b1 00 00 00    	je     e9 <jocker_ioctl+0xe9>
  38:	81 fb 03 6a 10 40    	cmp    ebx,0x40106a03
  3e:	74 57                	je     97 <jocker_ioctl+0x97>
  40:	48 c7 c5 e7 ff ff ff 	mov    rbp,0xffffffffffffffe7
  47:	81 fb 01 6a 00 00    	cmp    ebx,0x6a01
  4d:	75 32                	jne    81 <jocker_ioctl+0x81>
  4f:	48 83 3d 00 00 00 00 	cmp    QWORD PTR [rip+0x0],0x0        # 57 <jocker_ioctl+0x57>
  56:	00 
  57:	0f 85 ab 00 00 00    	jne    108 <jocker_ioctl+0x108>
  5d:	48 8b 3d 00 00 00 00 	mov    rdi,QWORD PTR [rip+0x0]        # 64 <jocker_ioctl+0x64>
  64:	ba 00 04 00 00       	mov    edx,0x400
  69:	be c0 0d 40 00       	mov    esi,0x400dc0
  6e:	e8 00 00 00 00       	call   73 <jocker_ioctl+0x73>
  73:	48 89 05 00 00 00 00 	mov    QWORD PTR [rip+0x0],rax        # 7a <jocker_ioctl+0x7a>
  7a:	48 85 c0             	test   rax,rax
  7d:	74 7d                	je     fc <jocker_ioctl+0xfc>
  7f:	31 ed                	xor    ebp,ebp
  81:	48 c7 c7 00 00 00 00 	mov    rdi,0x0
  88:	e8 00 00 00 00       	call   8d <jocker_ioctl+0x8d>
  8d:	48 89 e8             	mov    rax,rbp
  90:	48 83 c4 10          	add    rsp,0x10
  94:	5b                   	pop    rbx
  95:	5d                   	pop    rbp
  96:	c3                   	ret
  97:	80 3d 00 00 00 00 00 	cmp    BYTE PTR [rip+0x0],0x0        # 9e <jocker_ioctl+0x9e>
  9e:	75 74                	jne    114 <jocker_ioctl+0x114>
  a0:	ba 10 00 00 00       	mov    edx,0x10
  a5:	48 89 ee             	mov    rsi,rbp
  a8:	48 89 e7             	mov    rdi,rsp
  ab:	e8 00 00 00 00       	call   b0 <jocker_ioctl+0xb0>
  b0:	48 85 c0             	test   rax,rax
  b3:	75 2b                	jne    e0 <jocker_ioctl+0xe0>
  b5:	48 8b 3d 00 00 00 00 	mov    rdi,QWORD PTR [rip+0x0]        # bc <jocker_ioctl+0xbc>
  bc:	48 85 ff             	test   rdi,rdi
  bf:	74 5f                	je     120 <jocker_ioctl+0x120>
  c1:	48 8b 74 24 08       	mov    rsi,QWORD PTR [rsp+0x8]
  c6:	48 03 3c 24          	add    rdi,QWORD PTR [rsp]
  ca:	ba 01 00 00 00       	mov    edx,0x1
  cf:	c6 05 00 00 00 00 01 	mov    BYTE PTR [rip+0x0],0x1        # d6 <jocker_ioctl+0xd6>
  d6:	e8 00 00 00 00       	call   db <jocker_ioctl+0xdb>
  db:	48 85 c0             	test   rax,rax
  de:	74 9f                	je     7f <jocker_ioctl+0x7f>
  e0:	48 c7 c5 f2 ff ff ff 	mov    rbp,0xfffffffffffffff2
  e7:	eb 98                	jmp    81 <jocker_ioctl+0x81>
  e9:	48 8b 3d 00 00 00 00 	mov    rdi,QWORD PTR [rip+0x0]        # f0 <jocker_ioctl+0xf0>
  f0:	48 85 ff             	test   rdi,rdi
  f3:	74 2b                	je     120 <jocker_ioctl+0x120>
  f5:	e8 00 00 00 00       	call   fa <jocker_ioctl+0xfa>
  fa:	eb 83                	jmp    7f <jocker_ioctl+0x7f>
  fc:	48 c7 c5 f4 ff ff ff 	mov    rbp,0xfffffffffffffff4
 103:	e9 79 ff ff ff       	jmp    81 <jocker_ioctl+0x81>
 108:	48 c7 c5 ef ff ff ff 	mov    rbp,0xffffffffffffffef
 10f:	e9 6d ff ff ff       	jmp    81 <jocker_ioctl+0x81>
 114:	48 c7 c5 ff ff ff ff 	mov    rbp,0xffffffffffffffff
 11b:	e9 61 ff ff ff       	jmp    81 <jocker_ioctl+0x81>
 120:	48 c7 c5 fe ff ff ff 	mov    rbp,0xfffffffffffffffe
 127:	e9 55 ff ff ff       	jmp    81 <jocker_ioctl+0x81>

Disassembly of section .init.text:

0000000000000000 <init_module>:
   0:	f3 0f 1e fa          	endbr64
   4:	48 c7 c7 00 00 00 00 	mov    rdi,0x0
   b:	e9 00 00 00 00       	jmp    10 <g_buf+0x8>

Disassembly of section .exit.text:

0000000000000000 <cleanup_module>:
   0:	f3 0f 1e fa          	endbr64
   4:	48 c7 c7 00 00 00 00 	mov    rdi,0x0
   b:	e9 00 00 00 00       	jmp    10 <g_buf+0x8>
