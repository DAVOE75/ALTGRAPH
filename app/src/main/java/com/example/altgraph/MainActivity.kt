package com.example.altgraph

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#09090B"))
            setPadding(48, 48, 48, 48)
        }

        val logo = ImageView(this).apply {
            setImageResource(R.drawable.ic_altigraph_logo)
            layoutParams = LinearLayout.LayoutParams(240, 240).apply {
                bottomMargin = 32
            }
        }

        val title = TextView(this).apply {
            text = getString(R.string.app_name)
            textSize = 28f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
        }

        val subtitle = TextView(this).apply {
            text = "v0.2 • Karoo Extension Active"
            textSize = 14f
            setTextColor(Color.parseColor("#A1A1AA"))
            gravity = Gravity.CENTER
            setPadding(0, 12, 0, 32)
        }

        val statusCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#18181B"))
            setPadding(32, 32, 32, 32)
        }

        val statusText = TextView(this).apply {
            text = "✓ Extension Registered & Running"
            textSize = 16f
            setTextColor(Color.parseColor("#22C55E"))
            gravity = Gravity.CENTER
        }

        val hintText = TextView(this).apply {
            text = "Configure data fields in Karoo Ride Profiles settings."
            textSize = 13f
            setTextColor(Color.parseColor("#A1A1AA"))
            gravity = Gravity.CENTER
            setPadding(0, 16, 0, 0)
        }

        statusCard.addView(statusText)
        statusCard.addView(hintText)

        rootLayout.addView(logo)
        rootLayout.addView(title)
        rootLayout.addView(subtitle)
        rootLayout.addView(statusCard)

        setContentView(rootLayout)
    }
}