import numpy as np

x = np.array([1,2,3,4,5])
y = np.array([5,7,9,11,13])
m_curr = b_curr = 0
n = len(x)
y_predicted = m_curr*x + b_curr
print(y_predicted)
ds = sum(x*(y-y_predicted))
print(ds)