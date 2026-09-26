import re

with open('app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'r', encoding='utf-8') as f:
    code = f.read()

old_block = """          if (mapZoomLevel != null) {
              lookaheadDist = when {
                  mapZoomLevel!! >= 17.0 -> 1000.0   // Max Zoom In -> 1km
                  mapZoomLevel!! >= 16.0 -> 2000.0   // Zoom In -> 2km
                  mapZoomLevel!! >= 15.0 -> 5000.0   // Normal -> 5km
                  mapZoomLevel!! >= 14.0 -> 10000.0  // Zoom Out -> 10km
                  mapZoomLevel!! >= 13.0 -> 20000.0
                  mapZoomLevel!! >= 10.0 -> 50000.0
                  else -> 100000.0                   // Max Zoom Out -> 100km
              }
          }"""

new_block = """          if (mapZoomLevel != null) {
              lookaheadDist = when {
                  mapZoomLevel!! >= 18.0 -> 150.0    // ~150m screen width
                  mapZoomLevel!! >= 17.5 -> 250.0
                  mapZoomLevel!! >= 17.0 -> 350.0    // ~350m screen width
                  mapZoomLevel!! >= 16.5 -> 500.0
                  mapZoomLevel!! >= 16.0 -> 700.0    // ~700m screen width
                  mapZoomLevel!! >= 15.0 -> 1500.0   // ~1.5km
                  mapZoomLevel!! >= 14.0 -> 3000.0   // ~3km
                  mapZoomLevel!! >= 13.0 -> 6000.0   // ~6km
                  mapZoomLevel!! >= 12.0 -> 12000.0  // ~12km
                  mapZoomLevel!! >= 11.0 -> 25000.0
                  mapZoomLevel!! >= 10.0 -> 50000.0
                  else -> 100000.0
              }
          }"""

if old_block in code:
    new_code = code.replace(old_block, new_block)
    with open('app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'w', encoding='utf-8') as f:
        f.write(new_code)
    print("Replaced!")
else:
    print("Could not find block!")
