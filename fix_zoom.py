import re

with open('app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'r', encoding='utf-8') as f:
    code = f.read()

start_str = "        if (mapZoomLevel != null) {"
end_str = "        } else if (smartZoomEnabled && baseLookahead < 100000.0) {"
start_idx = code.find(start_str)
end_idx = code.find(end_str)

new_block = """        if (mapZoomLevel != null) {
            // Calculated for Karoo 480px width at ~40 degrees latitude
            // mapWidthMeters = 57557280 / (2^zoom)
            lookaheadDist = when {
                mapZoomLevel!! >= 18.0 -> 220.0
                mapZoomLevel!! >= 17.5 -> 310.0
                mapZoomLevel!! >= 17.0 -> 440.0
                mapZoomLevel!! >= 16.5 -> 620.0
                mapZoomLevel!! >= 16.0 -> 880.0
                mapZoomLevel!! >= 15.5 -> 1240.0
                mapZoomLevel!! >= 15.0 -> 1750.0
                mapZoomLevel!! >= 14.5 -> 2480.0
                mapZoomLevel!! >= 14.0 -> 3500.0
                mapZoomLevel!! >= 13.0 -> 7000.0
                mapZoomLevel!! >= 12.0 -> 14000.0
                mapZoomLevel!! >= 11.0 -> 28000.0
                mapZoomLevel!! >= 10.0 -> 56000.0
                else -> 100000.0
            }
"""

new_code = code[:start_idx] + new_block + code[end_idx:]

with open('app/src/main/java/com/example/altgraph/AltimetriaStrategyCalculator.kt', 'w', encoding='utf-8') as f:
    f.write(new_code)
