from pathlib import Path
import re
source = Path("VaultDoorTraining.java").read_text()
match = re.search(r'password\.equals\("([^"]+)"\)',source)

if not match:
    raise ValueError("Password tidak di temukan")

print(f"academy{{{match.group(1)}}}")