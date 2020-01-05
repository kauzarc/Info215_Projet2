import matplotlib.pyplot as plt
import numpy as np

f = open("gmm_data.d")

x = []
y = []

for line in f.readlines():
    a, b = line.split()
    x.append((float)(a))
    y.append((float)(b))

plt.scatter(x, y)
plt.show()