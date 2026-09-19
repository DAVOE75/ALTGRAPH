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
            grade < 0.0 -> "#F3F4F6" // Gris claro casi blanco
            grade < 3.0 -> "#16A34A" // Verde intenso
            grade < 5.0 -> "#FFD600" // Amarillo vivo
            grade < 8.0 -> "#FF6D00" // Naranja intenso
            grade < 10.0 -> "#8B4513" // Marrón fuerte (SaddleBrown)
            grade < 13.0 -> "#D50000" // Rojo puro intenso
            grade <= 17.0 -> "#800000" // Granate oscuro
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
