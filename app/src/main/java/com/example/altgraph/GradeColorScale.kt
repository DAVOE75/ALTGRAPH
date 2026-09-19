package com.example.altgraph

import android.graphics.Color

object GradeColorScale {

    /**
     * Obtiene el código hexadecimal del color según la pendiente:
     * - < 0%: Gris claro tirando a blanco
     * - 0% a 3%: Verde
     * - 3% a 5%: Amarillo
     * - 5% a 8%: Naranja
     * - 8% a 10%: Marrón oscuro
     * - 10% a 13%: Rojo
     * - 13% a 17%: Granate
     * - > 17%: Negro
     */
    fun getColorHex(grade: Double): String {
        return when {
            grade < 0.0 -> "#FFFFFF" // Blanco puro para bajadas
            grade < 3.0 -> "#00FF00" // Verde puro flúor
            grade < 5.0 -> "#FFFF00" // Amarillo puro flúor
            grade < 8.0 -> "#FF6600" // Naranja puro eléctrico
            grade < 10.0 -> "#964B00" // Marrón puro intenso
            grade < 13.0 -> "#FF0000" // Rojo puro sangre
            grade <= 17.0 -> "#8B0000" // Granate rojo oscuro
            else -> "#000000"        // Negro (> 17%)
        }
    }

    /**
     * Color para telemetría de texto sobre fondo oscuro (evita texto negro sobre fondo negro)
     */
    fun getTelemetryColor(grade: Double): Int {
        return if (grade > 17.0) {
            Color.WHITE
        } else {
            Color.parseColor(getColorHex(grade))
        }
    }
}
