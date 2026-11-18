FROM ubuntu:22.04

RUN sed -i "s/http:\/\/archive.ubuntu.com/http:\/\/kr.archive.ubuntu.com/g" /etc/apt/sources.list
RUN sed -i "s/http:\/\/security.ubuntu.com/http:\/\/kr.archive.ubuntu.com/g" /etc/apt/sources.list
RUN dpkg --add-architecture i386
RUN apt-get update && \
    apt-get install -y libc6:i386 gdb socat && \
    rm -rf /var/lib/apt/lists/*

RUN useradd -u 1004 -m passcode_pwn

COPY passcode /home/passcode_pwn/passcode
COPY flag /home/passcode_pwn/flag

RUN chown -R root:passcode_pwn /home/passcode_pwn
RUN chmod 550 /home/passcode_pwn/passcode
RUN chmod 440 /home/passcode_pwn/flag

USER passcode_pwn
WORKDIR /home/passcode_pwn
CMD ["socat", "TCP-LISTEN:10004,reuseaddr,fork", "EXEC:/usr/bin/timeout -k 5 300 /home/passcode_pwn/passcode,pty,raw,echo=0,stderr"]
EXPOSE 10004
