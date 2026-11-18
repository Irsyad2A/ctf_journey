#!/bin/bash
HERE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

exec timeout -sKILL 120 qemu-system-x86_64 \
    -kernel "$HERE/bzImage" \
    -initrd "$HERE/rootfs.cpio.gz" \
    -m 64M \
    -smp cores=1,threads=1 \
    -cpu qemu64,+smep,+smap \
    -append "console=ttyS0 kaslr smep smap kpti=on quiet loglevel=0 oops=panic panic=1 root=/dev/sda" \
    -nographic \
    -no-reboot \
    -monitor /dev/null \
    -net nic,model=virtio \
    -net user
