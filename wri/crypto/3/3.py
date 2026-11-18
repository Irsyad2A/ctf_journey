m = 9815675221511745357742037228731970293732718577527424267525616689035890529277455835177241232710651163390164841764790656565971863151045772216490603617356733630268683762827109

lo, hi = 0, m
while lo < hi:
    mid = (lo + hi) // 2
    if mid**3 < m:
        lo = mid + 1
    else:
        hi = mid

hex_str = hex(lo)[2:]
if len(hex_str) % 2 != 0: hex_str = '0' + hex_str
print(bytes.fromhex(hex_str).decode())
