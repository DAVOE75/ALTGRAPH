package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altgraph", "0.4.62") {

    override val types: List<DataTypeImpl>
        get() {
            val list = mutableListOf(
                ClimbViewerDataType("altgraph"),
                AltimetriaGraphDataType("altgraph"),
                Altimetria3DGraphDataType("altgraph"),
                FatigueGradeDataField("altgraph"),
                ClimbPacingDataField("altgraph"),
                GradientTrendDataField("altgraph")
            )
            if (AppPreferences.getInstance(applicationContext).eliteRadarEnabled) {
                list.add(1, RouteBarDataType("altgraph"))
            }
            return list
        }
}