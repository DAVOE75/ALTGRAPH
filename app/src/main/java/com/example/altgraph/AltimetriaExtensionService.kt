package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altimetria_graph", "0.2") {

    override val types: List<DataTypeImpl>
        get() = listOf(
            AltimetriaGraphDataType("altimetria_graph"),
            ApmDataField("altimetria_graph"),
            ClimbPacingDataField("altimetria_graph"),
            GradientTrendDataField("altimetria_graph")
        )
}