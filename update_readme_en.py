import re

# Fix README_en.md
with open('c:/ALTGRAPH/README_en.md', 'r', encoding='utf-8') as f:
    content = f.read()

content = content.replace('# ALTGRAPH (v0.4.1 STABLE)', '# ALTGRAPH (v1.0.4 ELITE)')
content = content.replace('Suite of 5 Altimetry Visualization Models', 'Suite of 6 Altimetry Visualization Models')

new_notes = """
## 🐛 VERSION 1.0.4: MILLIMETRIC GPS ACCURACY 🐛

- **Critical GPS Synchronization Fix (Corner Cutting Fix)**: We have completely rewritten the distance calculation mathematical engine to include a *Dynamic Scalar Calibrator*. From now on, simplified projected distances (polylines) are stretched and adapted to match the real physical distances tracked by your Karoo unit on the road. The radar distance tracking now perfectly matches the telemetry, showing true slope percentages exactly under your front wheel, from kilometer zero to the end of your route.
- **Continuous Pointer Offset Fix (X-Axis)**: Resolved all X-Axis compression issues that caused the display to warn you of steep ramps or descents at the wrong time due to delayed pointer tracking.
- **Rendering Engine Update**: Optimized real-time dynamic Hairpin curve generation.

## 🐛 VERSION 1.0.3: DISTANCE & ROUTE CORRECTION 🐛

- **Altimetry Radar Fix:** Resolved a critical bug that caused the radar and the rider progress marker to erroneously jump back to kilometer 0 (km 0) at the start of a climb or after riding several kilometers.
- **Continuous route progress:** The application now properly compensates for the internal route (polyline) simplifications made by the Karoo GPS. Ridden distance, the bottom bar, and notifications will maintain their continuous global progress throughout your ride.

"""

content = content.replace('## 🐛 VERSION 1.0.2:', new_notes + '## 🐛 VERSION 1.0.2:')

with open('c:/ALTGRAPH/README_en.md', 'w', encoding='utf-8') as f:
    f.write(content)
