import re

with open("opcodes.txt") as fl:
    text = fl.read()

lines = text.split("\n")

hd = list("0123456789abcdef")
table = {}
hi = 0
for line in lines:
    codes = line.split("\t")
    lo = 0
    for code in codes:
        if code != "---":
            table["0x"+hd[hi]+hd[lo]] = code
            print("0x"+hd[hi]+hd[lo],code)
        lo+=1
    hi+=1

output_lines = []
for i in range(256):
    key = '0x%02x' % i
    if key in table:
        output_lines.append(table[key])
    else:
        output_lines.append("null")

with open("optable.txt","w") as fl:
    fl.write("\n".join(output_lines))

