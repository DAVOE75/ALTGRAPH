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
            grade < 0.0 -> "#E5E7EB" // Gris claro tirando a blanco
            grade < 3.0 -> "#65A30D" // Verde
            grade < 5.0 -> "#FACC15" // Amarillo
            grade < 8.0 -> "#F97316" // Naranja
            grade < 10.0 -> "#78350F" // Marrón oscuro
            grade < 13.0 -> "#DC2626" // Rojo
            grade <= 17.0 -> "#7F1D1D" // Granate
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
