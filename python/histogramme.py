import matplotlib.pyplot as plt
import numpy as np

f1 = open("result/histo1D_histo.txt")

v1 = []
v2 = []

for line in f1.readlines():
    a, b = line.split()

    v1.append((float)(a))
    v2.append((float)(b))


v1 = np.array(v1)
v2 = np.array(v2)

v2 *= (float)(v1.size) / (v2.sum() * (v1[-1] - v1[0]))

plt.plot(v1, v2)

f2 = open("result/histo1D_gauss")

x = np.arange(-5., 10., 0.1)
buff = []
for line in f2.readlines():
    a, b, c = line.split()
    m = (float)(a)
    variance = (float)(b)
    roh = (float)(c)

    y = x - m
    y /= np.sqrt(variance)
    y = y * y
    y /= -2
    y = np.exp(y)

    y /= np.sqrt(variance) * np.sqrt(2 * np.pi)
    buff.append(roh * y)

plt.plot(x, buff[0])
plt.plot(x, buff[1])

# plt.plot(x, (buff[0]+buff[1]))

plt.show()
