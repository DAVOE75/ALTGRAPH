package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altimetria_graph", "1.0") {
    override val types: List<DataTypeImpl>
        get() = listOf(
            ApmDataField("altimetria_graph")
        )
}