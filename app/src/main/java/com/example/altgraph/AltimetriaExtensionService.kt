package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altgraph", "0.2") {

    override val types: List<DataTypeImpl>
        get() = listOf(
            AltimetriaGraphDataType("altgraph"),
            ApmDataField("altgraph"),
            ClimbPacingDataField("altgraph"),
            GradientTrendDataField("altgraph")
        )
}