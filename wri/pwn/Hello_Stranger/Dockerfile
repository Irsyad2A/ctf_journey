FROM debian:bookworm-slim AS build

RUN apt-get update && apt-get install -y --no-install-recommends gcc libc6-dev && rm -rf /var/lib/apt/lists/*
WORKDIR /build
COPY chall.c .
RUN gcc -O0 -fno-stack-protector -fno-pie -no-pie -z noexecstack -o chall chall.c

FROM debian:bookworm-slim

RUN apt-get update && apt-get install -y --no-install-recommends socat && rm -rf /var/lib/apt/lists/* \
    && useradd -m -s /usr/sbin/nologin chall
COPY --from=build /build/chall /home/chall/chall
RUN chmod 0555 /home/chall/chall
USER chall
WORKDIR /home/chall
EXPOSE 1337
CMD ["socat", "TCP-LISTEN:1337,reuseaddr,fork", "EXEC:/home/chall/chall,stderr"]
