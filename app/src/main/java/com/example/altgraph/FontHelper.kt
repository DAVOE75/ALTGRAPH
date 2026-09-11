package com.example.altgraph

import android.graphics.Typeface

object FontHelper {

    data class FontOption(
        val key: String,
        val labelEs: String,
        val labelEn: String
    )

    val options = listOf(
        FontOption("sans-serif-condensed", "Condensed (Estrecha/Alta)", "Condensed (Narrow/Tall)"),
        FontOption("sans-serif", "Sans-Serif (Estándar)", "Sans-Serif (Standard)"),
        FontOption("sans-serif-medium", "Sans Medium", "Sans Medium"),
        FontOption("sans-serif-black", "Sans Black (Gruesa)", "Sans Black (Heavy)"),
        FontOption("monospace", "Monospace Digital", "Monospace Digital")
    )

    fun getTypeface(familyKey: String, style: Int = Typeface.BOLD): Typeface {
        return when (familyKey) {
            "sans-serif-condensed" -> Typeface.create("sans-serif-condensed", style)
            "sans-serif" -> Typeface.create("sans-serif", style)
            "sans-serif-medium" -> Typeface.create("sans-serif-medium", style)
            "sans-serif-black" -> Typeface.create("sans-serif-black", style)
            "monospace" -> Typeface.create(Typeface.MONOSPACE, style)
            else -> Typeface.create("sans-serif-condensed", style)
        }
    }
}