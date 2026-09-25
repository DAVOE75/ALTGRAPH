package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altgraph", "0.4.63") {

    override val types: List<DataTypeImpl> by lazy {
        listOf(
            ClimbViewerDataType("altgraph"),
            RouteBarDataType("altgraph"),
            AltimetriaGraphDataType("altgraph"),
            Altimetria3DGraphDataType("altgraph"),
            FatigueGradeDataField("altgraph"),
            ClimbPacingDataField("altgraph"),
            GradientTrendDataField("altgraph")
        )
    }
}