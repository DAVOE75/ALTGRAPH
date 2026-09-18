package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altgraph", "0.4.0") {

    override val types: List<DataTypeImpl> by lazy {
        listOf(
            AltimetriaGraphDataType("altgraph"),
            Altimetria3DGraphDataType("altgraph"),
            FatigueGradeDataField("altgraph"),
            ClimbPacingDataField("altgraph"),
            GradientTrendDataField("altgraph")
        )
    }
}