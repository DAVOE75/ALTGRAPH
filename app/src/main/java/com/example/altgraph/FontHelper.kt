package com.example.altgraph

import android.graphics.Paint
import android.graphics.Typeface

object FontHelper {

    data class FontOption(
        val key: String,
        val labelEs: String,
        val labelEn: String,
        val scaleX: Float
    )

    val options = listOf(
        FontOption("sans-serif-condensed", "Condensed (Estrecha/Alta)", "Condensed (Narrow/Tall)", 0.72f),
        FontOption("sans-serif", "Sans-Serif (Estándar)", "Sans-Serif (Standard)", 1.0f),
        FontOption("sans-serif-medium", "Sans Medium", "Sans Medium", 1.0f),
        FontOption("sans-serif-black", "Sans Black (Extra Gruesa)", "Sans Black (Heavy)", 1.15f),
        FontOption("monospace", "Monospace Digital", "Monospace Digital", 1.0f)
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