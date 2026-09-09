package com.example.altgraph

import android.content.Context
import io.hammerhead.karooext.KarooSystemService
import io.hammerhead.karooext.extension.KarooExtension
import io.hammerhead.karooext.models.DataType
import io.hammerhead.karooext.models.OnStreamState
import io.hammerhead.karooext.models.StreamState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.hypot
import kotlin.math.roundToInt

data class RoutePoint(
    val latitude: Double,
    val longitude: Double,
    val elevation: Double,
    val distance: Double
)

class AltimetriaDataField(private val context: Context) : KarooExtension("altimetria_graph", "0.1") {

    private val karooSystem = KarooSystemService(context)
    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null
    private var altimetriaView: AltimetriaView? = null

    private var speedConsumerId: String? = null
    private var elevationConsumerId: String? = null

    private var currentLatitude = 0.0
    private var currentLongitude = 0.0
    private var currentSpeed = 0.0
    private var currentElevation = 0.0

    private var routePoints: List<RoutePoint> = emptyList()

    private val userBlockSize = 100.0
    private val minBlockSize = 100.0
    private val thresholdAttack = 10.0

    init {
        val customView = AltimetriaView(context)
        altimetriaView = customView

        karooSystem.connect { connected ->
            if (connected) {
                // Escucha de velocidad mediante OnStreamState.StartStreaming
                speedConsumerId = karooSystem.addConsumer(
                    params = OnStreamState.StartStreaming(DataType.Type.SPEED)
                ) { event: OnStreamState ->
                    val state = event.state
                    if (state is StreamState.Streaming) {
                        currentSpeed = state.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                    }
                }

                // Escucha de elevación barométrica
                elevationConsumerId = karooSystem.addConsumer(
                    params = OnStreamState.StartStreaming(DataType.Type.PRESSURE_ELEVATION_CORRECTION)
                ) { event: OnStreamState ->
                    val state = event.state
                    if (state is StreamState.Streaming) {
                        currentElevation = state.dataPoint.values[DataType.Field.SINGLE] ?: 0.0
                    }
                }

                // Ciclo periódico para cálculo y refresco de vista
                job = scope.launch {
                    while (true) {
                        val strategy = calculateStrategy()

                        altimetriaView?.updateStrategyData(
                            remainingDistance = strategy.remainingDistance,
                            timeToSummit = strategy.timeToSummit,
                            avgGrade = strategy.avgGrade,
                            currentZoneColor = getZoneColor(currentElevation),
                            nextBlocks = strategy.nextBlocks,
                            attackAlert = strategy.attackAlert,
                            blockSizeMeters = strategy.blockSizeMeters
                        )
                        delay(1000)
                    }
                }
            }
        }
    }

    fun setRoutePoints(points: List<RoutePoint>) {
        this.routePoints = points
    }

    fun updateCurrentLocation(lat: Double, lng: Double) {
        this.currentLatitude = lat
        this.currentLongitude = lng
    }

    private fun calculateStrategy(): StrategyData {
        if (routePoints.isEmpty() || currentSpeed <= 0.1) {
            ClimbStateManager.updateApm(0)
            return StrategyData(0.0, 0L, 0.0, emptyList(), false, userBlockSize, 0)
        }

        val blockSize = if (userBlockSize < minBlockSize) minBlockSize else userBlockSize

        var nearestIndex = 0
        var minDistance = Double.MAX_VALUE
        routePoints.forEachIndexed { index, point ->
            val dist = hypot(point.latitude - currentLatitude, point.longitude - currentLongitude)
            if (dist < minDistance) {
                minDistance = dist
                nearestIndex = index
            }
        }

        val totalDistanceRemaining = routePoints.drop(nearestIndex).sumOf { it.distance }
        val endElevation = routePoints.last().elevation
        val elevationGainRemaining = endElevation - currentElevation
        val avgGradeRemaining = if (totalDistanceRemaining > 0) (elevationGainRemaining / totalDistanceRemaining) * 100.0 else 0.0
        val secondsRemaining = (totalDistanceRemaining / currentSpeed).toLong()

        val nextBlocks = mutableListOf<Float>()
        var attack = false
        var accumulatedApm = 0.0

        var currentBlockStartDistance = routePoints[nearestIndex].distance
        var currentBlockStartElevation = routePoints[nearestIndex].elevation

        for (i in nearestIndex + 1 until routePoints.size) {
            val point = routePoints[i]
            val distanceDiff = point.distance - currentBlockStartDistance

            if (distanceDiff >= blockSize) {
                val grade = ((point.elevation - currentBlockStartElevation) / distanceDiff) * 100.0

                accumulatedApm += ApmCalculator.calculateSegmentApm(distanceDiff, grade)

                if (nextBlocks.size < 10) {
                    nextBlocks.add(grade.toFloat())
                    if (grade > thresholdAttack) {
                        attack = true
                    }
                }

                currentBlockStartDistance = point.distance
                currentBlockStartElevation = point.elevation
            }
        }

        val totalApm = accumulatedApm.roundToInt()
        ClimbStateManager.updateApm(totalApm)

        return StrategyData(
            remainingDistance = totalDistanceRemaining,
            timeToSummit = secondsRemaining,
            avgGrade = avgGradeRemaining,
            nextBlocks = nextBlocks,
            attackAlert = attack,
            blockSizeMeters = blockSize,
            totalApm = totalApm
        )
    }

    private fun getZoneColor(currentElevation: Double): String {
        return "VERDE"
    }

    data class StrategyData(
        val remainingDistance: Double,
        val timeToSummit: Long,
        val avgGrade: Double,
        val nextBlocks: List<Float>,
        val attackAlert: Boolean,
        val blockSizeMeters: Double,
        val totalApm: Int
    )

    fun stopExtension() {
        job?.cancel()
        speedConsumerId?.let { karooSystem.removeConsumer(it) }
        elevationConsumerId?.let { karooSystem.removeConsumer(it) }
        karooSystem.disconnect()
    }
}