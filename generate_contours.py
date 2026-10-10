import numpy as np
import matplotlib.pyplot as plt
from scipy.ndimage import gaussian_filter

# Generate random noise
np.random.seed(42)
Z = np.random.rand(100, 100)
Z = gaussian_filter(Z, sigma=5)

fig, ax = plt.subplots(figsize=(6, 4))
fig.patch.set_facecolor('black')
ax.set_facecolor('black')
ax.axis('off')

# Plot contours
contour = ax.contour(Z, levels=15, colors='#555555', linewidths=1.5)

plt.subplots_adjust(top=1, bottom=0, right=1, left=0, hspace=0, wspace=0)
plt.margins(0,0)
plt.savefig('app/src/main/res/drawable-nodpi/contour_bg.png', facecolor='black', bbox_inches='tight', pad_inches=0, dpi=100)
