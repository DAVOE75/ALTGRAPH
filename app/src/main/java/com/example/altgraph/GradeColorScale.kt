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
            grade <= -99.0 -> "#111111" // Fuera de ruta (Negro)
            grade <= -10.0 -> "#041E42" // Descenso pronunciado (Azul marino muy oscuro)
            grade <= -5.0 -> "#004B87" // Descenso medio (Azul oscuro)
            grade <= -2.0 -> "#0072CE" // Descenso suave (Azul medio)
            grade < 0.0 -> "#41B6E6" // Falso llano bajada (Azul claro celeste)
            grade < 3.0 -> "#388E3C" // Verde bosque intenso (sin ser flúor)
            grade < 5.0 -> "#FBC02D" // Amarillo fuerte
            grade < 8.0 -> "#F57C00" // Naranja intenso
            grade < 10.0 -> "#E65100" // Naranja oscuro / teja
            grade < 13.0 -> "#D32F2F" // Rojo intenso
            grade <= 17.0 -> "#B71C1C" // Granate rojo oscuro
            else -> "#000000"        // Negro (> 17%)
        }
    }

    /**
     * Color scale specifically for climbs (White to Intense Red).
     * Descents remain blue.
     */
    fun getClimbColorHex(grade: Double): String {
        return when {
            grade <= -99.0 -> "#111111" // Fuera de ruta (Negro)
            grade <= -10.0 -> "#041E42" // Descenso pronunciado
            grade <= -5.0 -> "#004B87" // Descenso medio
            grade <= -2.0 -> "#0072CE" // Descenso suave
            grade < 0.0 -> "#41B6E6" // Falso llano bajada
            grade < 3.0 -> "#FFFFFF" // Blanco llano
            grade < 5.0 -> "#FEF08A" // Amarillo muy pálido
            grade < 8.0 -> "#FACC15" // Amarillo fuerte
            grade < 10.0 -> "#FB923C" // Naranja
            grade < 13.0 -> "#EA580C" // Naranja oscuro / Rojo claro
            grade <= 17.0 -> "#DC2626" // Rojo intenso
            else -> "#7F1D1D"        // Rojo muy oscuro / Granate oscuro (> 17%)
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
