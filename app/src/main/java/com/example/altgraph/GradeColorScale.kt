package com.example.altgraph

import android.graphics.Color

object GradeColorScale {
    // 15 tramos continuos de 1 en 1 desde el 0% al 15%:
    // Blanco -> Crema -> Amarillo muy claro -> Amarillo claro -> Amarillo -> Dorado -> Naranja -> Rojo intenso
    private val TRAMOS_0_A_15 = arrayOf(
        "#FFFFFF", // 0% a 1%: Blanco
        "#FEF9C3", // 1% a 2%: Crema / blanco amarillento suave
        "#FEF08A", // 2% a 3%: Amarillo muy claro
        "#FDE047", // 3% a 4%: Amarillo claro
        "#FACC15", // 4% a 5%: Amarillo medio
        "#EAB308", // 5% a 6%: Amarillo más oscuro / dorado
        "#F59E0B", // 6% a 7%: Amarillo anaranjado
        "#FB923C", // 7% a 8%: Naranja claro / ámbar
        "#F97316", // 8% a 9%: Naranja
        "#EA580C", // 9% a 10%: Naranja intenso
        "#E03E1A", // 10% a 11%: Naranja rojizo / bermellón
        "#EA2E1A", // 11% a 12%: Rojo anaranjado
        "#E02424", // 12% a 13%: Rojo vivo
        "#DC2626", // 13% a 14%: Rojo puro
        "#B91C1C"  // 14% a 15%: Rojo intenso
    )

    const val COLOR_DESCENSO = "#1D4ED8"     // Azul para descensos o % negativos
    const val COLOR_ROJO_INTENSO = "#991B1B" // Rojo intenso para > 15% hasta 20%
    const val COLOR_NEGRO = "#000000"        // Negro para > 20%

    /**
     * Obtiene el código hexadecimal del color según la pendiente:
     * - < 0%: Azul
     * - 0% a 15%: Degradado suave en 15 tramos de 1 en 1 (blanco -> amarillo claro -> amarillo oscuro -> naranja -> rojo intenso)
     * - > 15% hasta 20%: Rojo intenso
     * - > 20%: Negro
     */
    fun getColorHex(grade: Double): String {
        return when {
            grade < 0.0 -> COLOR_DESCENSO
            grade > 20.0 -> COLOR_NEGRO
            grade >= 15.0 -> COLOR_ROJO_INTENSO
            else -> {
                val tramo = grade.toInt().coerceIn(0, 14)
                TRAMOS_0_A_15[tramo]
            }
        }
    }

    /**
     * Color para telemetría de texto sobre fondo oscuro (evita texto negro sobre fondo negro)
     */
    fun getTelemetryColor(grade: Double): Int {
        return if (grade > 20.0) {
            Color.WHITE
        } else {
            Color.parseColor(getColorHex(grade))
        }
    }
}
