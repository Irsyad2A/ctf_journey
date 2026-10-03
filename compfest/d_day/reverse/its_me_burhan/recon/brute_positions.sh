#!/bin/bash

set -u

CP=".:../burhanquest.jar"
CHARSET="ABCDEFGHIJKLMNOPQRSTUVWXYZ234567"

rm -f candidates.txt

echo "[+] Building candidate table..."

for pos in $(seq 0 15); do
    echo
    echo "===== POSITION $pos ====="

    for ((j=0; j<${#CHARSET}; j++)); do
        ch="${CHARSET:$j:1}"

        pw="AAAAAAAAAAAAAAAA"
        pw="${pw:0:$pos}${ch}${pw:$((pos+1))}"

        # Call verifier only.
        out=$(java -cp "$CP" VerifyPassword "$pw" 2>/dev/null)

        if echo "$out" | grep -q "result   = true"; then
            echo "[MATCH] pos=$pos char=$ch pw=$pw"
            echo "$pos $ch $pw" >> candidates.txt
        fi
    done
done

echo
echo "===== RESULTS ====="
cat candidates.txt 2>/dev/null || true
