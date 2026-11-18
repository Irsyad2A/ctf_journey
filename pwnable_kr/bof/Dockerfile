FROM ubuntu:22.04

RUN sed -i "s/http:\/\/archive.ubuntu.com/http:\/\/kr.archive.ubuntu.com/g" /etc/apt/sources.list
RUN sed -i "s/http:\/\/security.ubuntu.com/http:\/\/kr.archive.ubuntu.com/g" /etc/apt/sources.list
RUN dpkg --add-architecture i386
RUN apt-get update && \
    apt-get install -y libc6:i386 gdb socat && \
    rm -rf /var/lib/apt/lists/*

RUN useradd -u 1003 -m bof_pwn

COPY bof /home/bof_pwn/bof
COPY flag /home/bof_pwn/flag

RUN chown -R root:bof_pwn /home/bof_pwn
RUN chmod 550 /home/bof_pwn/bof
RUN chmod 440 /home/bof_pwn/flag

USER bof_pwn
WORKDIR /home/bof_pwn
CMD ["socat", "TCP-LISTEN:10003,reuseaddr,fork", "EXEC:/usr/bin/timeout -k 5 300 /home/bof_pwn/bof,pty,stderr,setsid,sigint,sane,echo=0"]
EXPOSE 10003
