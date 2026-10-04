package com.example.altgraph

import io.hammerhead.karooext.KarooSystemService
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

    private var mapOverlayManager: MapOverlayManager? = null
    private var karooSystem: KarooSystemService? = null
    private val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        karooSystem = KarooSystemService(applicationContext)
        karooSystem?.connect { }
    }

    override fun startMap(emitter: io.hammerhead.karooext.internal.Emitter<io.hammerhead.karooext.models.MapEffect>) {
        val sys = karooSystem ?: return
        mapOverlayManager = MapOverlayManager(applicationContext, scope, sys)
        mapOverlayManager?.start(emitter)
        
        emitter.setCancellable {
            mapOverlayManager?.stop()
        }
    }

    override fun onDestroy() {
        mapOverlayManager?.stop()
        karooSystem?.disconnect()
        super.onDestroy()
    }
}