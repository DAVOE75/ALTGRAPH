package com.example.altgraph

import android.graphics.Paint
import android.graphics.Typeface
import java.util.Locale

object FontHelper {

    data class FontOption(
        val key: String,
        val labelEs: String,
        val labelEn: String,
        val labelFr: String,
        val labelIt: String,
        val labelDe: String,
        val scaleX: Float
    ) {
        fun getLocalizedLabel(): String {
            val lang = Locale.getDefault().language.lowercase()
            return when {
                lang.startsWith("es") -> labelEs
                lang.startsWith("fr") -> labelFr
                lang.startsWith("it") -> labelIt
                lang.startsWith("de") -> labelDe
                else -> labelEn
            }
        }
    }

    val options = listOf(
        FontOption("sans-serif-condensed", "Condensed (Estrecha/Alta)", "Condensed (Narrow/Tall)", "Condensed (Étroite)", "Condensed (Stretta)", "Condensed (Schmal)", 0.72f),
        FontOption("sans-serif", "Sans-Serif (Estándar)", "Sans-Serif (Standard)", "Sans-Serif (Standard)", "Sans-Serif (Standard)", "Sans-Serif (Standard)", 1.0f),
        FontOption("sans-serif-medium", "Sans Medium", "Sans Medium", "Sans Medium", "Sans Medium", "Sans Medium", 1.0f),
        FontOption("sans-serif-black", "Sans Black (Extra Gruesa)", "Sans Black (Heavy)", "Sans Black (Épaisse)", "Sans Black (Pesante)", "Sans Black (Extradick)", 1.15f),
        FontOption("monospace", "Monospace Digital", "Monospace Digital", "Monospacé Digital", "Monospace Digitale", "Monospace Digital", 1.0f)
    )

    fun getTypeface(familyKey: String, style: Int = Typeface.BOLD): Typeface {
        return try {
            val base = when (familyKey) {
                "sans-serif-condensed" -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
                "sans-serif" -> Typeface.SANS_SERIF
                "sans-serif-medium" -> Typeface.create("sans-serif-medium", Typeface.NORMAL)
                "sans-serif-black" -> Typeface.create("sans-serif-black", Typeface.NORMAL)
                "monospace" -> Typeface.MONOSPACE
                else -> Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            }
            Typeface.create(base, style)
        } catch (e: Exception) {
            Typeface.create("sans-serif-condensed", style)
        }
    }

    fun getScaleX(familyKey: String): Float {
        return options.find { it.key == familyKey }?.scaleX ?: 0.72f
    }

    fun applyFontToPaint(paint: Paint, familyKey: String, style: Int = Typeface.BOLD) {
        paint.typeface = getTypeface(familyKey, style)
        paint.textScaleX = getScaleX(familyKey)
    }
}