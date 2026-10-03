#!/bin/bash

echo '===== ALL p.* CALLERS ====='
grep -nE \
'invoke(static|virtual|interface|special)?.*Method p\.' \
ALL.javap

echo
echo '===== ALL x.* CALLERS ====='
grep -nE \
'invoke(static|virtual|interface|special)?.*Method x\.' \
ALL.javap

echo
echo '===== l.f READS / WRITES ====='
grep -nE \
'getfield .*Field f:Ljava/lang/String;|putfield .*Field f:Ljava/lang/String;' \
ALL.javap

echo
echo '===== l.a() FLAG BYTE TRANSFORM ====='
grep -n -A560 -B15 \
'public byte\[\] a();' \
l_real.javap
