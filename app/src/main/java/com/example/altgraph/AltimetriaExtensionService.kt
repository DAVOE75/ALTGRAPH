package com.example.altgraph

import io.hammerhead.karooext.extension.DataTypeImpl
import io.hammerhead.karooext.extension.KarooExtension

class AltimetriaExtensionService : KarooExtension("altimetria_graph", "0.1") {
    override val types: List<DataTypeImpl>
        get() = listOf(
            ApmDataField("altimetria_graph")
        )
}