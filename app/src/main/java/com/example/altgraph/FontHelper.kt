package com.example.altgraph

import android.graphics.Typeface

object FontHelper {

    data class FontOption(
        val key: String,
        val labelEs: String,
        val labelEn: String
    )

    val options = listOf(
        FontOption("sans-serif-condensed", "Condensed (Estrecha)", "Condensed (Narrow/Tall)"),
        FontOption("sans-serif", "Sans-Serif (Estándar)", "Sans-Serif (Standard)"),
        FontOption("sans-serif-medium", "Sans Medium", "Sans Medium"),
        FontOption("sans-serif-black", "Sans Black (Gruesa)", "Sans Black (Heavy)"),
        FontOption("monospace", "Monospace Digital", "Monospace Digital")
    )

    fun getTypeface(familyKey: String, style: Int = Typeface.BOLD): Typeface {
        return try {
            Typeface.create(familyKey, style)
        } catch (e: Exception) {
            Typeface.create("sans-serif-condensed", style)
        }
    }
}