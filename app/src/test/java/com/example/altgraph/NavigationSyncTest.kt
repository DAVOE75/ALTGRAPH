package com.example.altgraph

import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnNavigationState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationSyncTest {

    private fun route(polyline: String) = OnNavigationState.NavigationState.NavigatingRoute(
        routePolyline = polyline,
        routeDistance = 2000.0,
        routeElevationPolyline = null,
        rejoinPolyline = null,
        rejoinDistance = null,
        name = "Test",
        reversed = false,
        breadcrumb = false,
        pois = emptyList(),
        climbs = emptyList()
    )

    private val goodPolyline = PolylineCodec.encode(listOf(40.0 to -3.0, 40.009 to -3.0, 40.018 to -3.0), 1e5)

    @Test
    fun navigatingRouteMatchesLegacy3DSequence() {
        val state = route(goodPolyline)
        val a = AltimetriaStrategyCalculator()
        NavigationSync.applyNavigation(a, state)

        // Secuencia antigua del campo 3D
        val b = AltimetriaStrategyCalculator()
        b.isNavigatingRoute = true
        b.activeRouteName = state.name
        b.syncRouteDistance(state.routeDistance)
        b.setRouteFromPolyline(state.routePolyline)
        b.setRouteElevationProfile(state.routeElevationPolyline)
        b.setRoutePois(state.pois)
        b.syncRouteClimbs("route:${state.name}", emptyList())
        b.syncRouteDistance(state.routeDistance)

        assertTrue(a.isNavigatingRoute)
        assertEquals(b.isNavigatingRoute, a.isNavigatingRoute)
        assertEquals("Test", a.activeRouteName)
        assertEquals(b.activeRouteName, a.activeRouteName)
        assertEquals(b.routePoints.size, a.routePoints.size)
        assertEquals(b.currentRouteDistance, a.currentRouteDistance, 0.0)
        assertEquals(b.routeClimbs.size, a.routeClimbs.size)
        assertEquals(
            b.calculateStrategy(advance = false).remainingDistance,
            a.calculateStrategy(advance = false).remainingDistance,
            0.0
        )
    }

    @Test
    fun idleClearsRoute() {
        val a = AltimetriaStrategyCalculator()
        NavigationSync.applyNavigation(a, route(goodPolyline))
        assertTrue(a.routePoints.isNotEmpty())
        NavigationSync.applyNavigation(a, OnNavigationState.NavigationState.Idle)
        assertTrue(a.routePoints.isEmpty())
    }

    @Test
    fun ascentRemainingKey() {
        val a = AltimetriaStrategyCalculator()
        NavigationSync.applyStream(a, DataType.Type.ELEVATION_REMAINING, mapOf(DataType.Field.ASCENT_REMAINING to 321.0))
        assertEquals(321.0, a.elevationRemaining, 0.0)
    }

    @Test
    fun malformedRouteDoesNotThrow() {
        val a = AltimetriaStrategyCalculator()
        NavigationSync.applyNavigation(a, route(""))
        NavigationSync.applyNavigation(a, route("@@@"))
    }
}
