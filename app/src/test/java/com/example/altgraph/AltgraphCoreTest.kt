package com.example.altgraph

import io.hammerhead.karooext.models.OnNavigationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AltgraphCoreTest {

    private fun accumulated(c: AltimetriaStrategyCalculator): Double {
        val f = AltimetriaStrategyCalculator::class.java.getDeclaredField("liveDistanceAccumulated")
        f.isAccessible = true
        return (f.get(c) as Number).toDouble()
    }

    @Test
    fun tickAdvancesOncePerTickRegardlessOfReaders() {
        val core = AltgraphCore()
        core.calculator.currentSpeed = 10.0
        val snap = core.tick(null)
        val dist = accumulated(core.calculator)

        core.mapZoomLevel = 14.0
        core.pan3dMeters = 500.0
        repeat(3) {
            Thread.sleep(50)
            core.strategyForRouteBar(null, 120, snap)
            core.strategyFor3D(null, snap)
        }
        assertEquals(dist, accumulated(core.calculator), 0.0)
    }

    @Test
    fun routeBarZoomDoesNotLeakIntoTick() {
        val core = AltgraphCore()
        val twin = AltimetriaStrategyCalculator()
        val snap = core.tick(null)
        twin.calculateStrategy(null) // mismo número de ticks que el core
        core.mapZoomLevel = 14.0
        core.strategyForRouteBar(null, 120, snap)
        assertNull(core.calculator.mapZoomLevel)
        // El zoom de la barra no se cuela en el tick: igual que un calculador gemelo sin zoom
        core.mapZoomLevel = 14.0
        assertEquals(twin.calculateStrategy(null), core.tick(null).strategy)
    }

    @Test
    fun panOnlyAffects3D() {
        val core = AltgraphCore()
        val poly = PolylineCodec.encode(listOf(40.0 to -3.0, 40.0225 to -3.0, 40.045 to -3.0), 1e5)
        NavigationSync.applyNavigation(
            core.calculator,
            OnNavigationState.NavigationState.NavigatingRoute(
                routePolyline = poly, routeDistance = 5000.0, routeElevationPolyline = null,
                rejoinPolyline = null, rejoinDistance = null, name = "T", reversed = false,
                breadcrumb = false, pois = emptyList(), climbs = emptyList()
            )
        )
        val snap = core.tick(null)
        core.pan3dMeters = 1000.0
        val s3d = core.strategyFor3D(null, snap)
        assertNotEquals(snap.strategy.windowStartMeters, s3d.windowStartMeters, 0.0)
        assertEquals(0.0, core.calculator.manualPanOffsetMeters, 0.0)
    }
}
