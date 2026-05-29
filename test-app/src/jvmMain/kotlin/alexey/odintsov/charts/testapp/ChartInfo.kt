package alexey.odintsov.charts.testapp

import alexey.odintsov.charts.model.ChartData
import alexey.odintsov.charts.model.ChartKey
import alexey.odintsov.charts.model.ChartType
import alexey.odintsov.charts.model.DurationChartData
import alexey.odintsov.charts.model.DurationEntry
import alexey.odintsov.charts.model.EventEntry
import alexey.odintsov.charts.model.EventsChartData
import alexey.odintsov.charts.model.MinMaxChartData
import alexey.odintsov.charts.model.MinMaxEntry
import alexey.odintsov.charts.model.PercentageChartData
import alexey.odintsov.charts.model.PercentageEntry
import alexey.odintsov.charts.model.SingleStateChartData
import alexey.odintsov.charts.model.SingleStateEntry
import alexey.odintsov.charts.model.StateChartData
import alexey.odintsov.charts.model.StateEntry
import alexey.odintsov.charts.model.StringKey
import kotlin.math.cos
import kotlin.math.sin

data class ChartInfo<out T>(
    val keys: List<ChartKey>,
    val chartType: ChartType,
    val entries: ChartData<T>?,
) {
    companion object {

        fun getMinMaxData(): ChartInfo<Float> {
            val key1 = StringKey("a")
            val key2 = StringKey("b")
            val keys = listOf(key1, key2)

            val data = MinMaxChartData<Float>().apply {
                for (i in 0..100) {
                    val time = i * 100_000L
                    val angle = i * 0.1
                    addEntry(key1, MinMaxEntry(time, (sin(angle) * 10 + 20).toFloat()))
                    addEntry(key2, MinMaxEntry(time, (cos(angle) * 10 + 20).toFloat()))
                }
            }

            return ChartInfo(keys, ChartType.MinMax, data)
        }

        fun getPercentageData(): ChartInfo<Float> {
            val key1 = StringKey("a")
            val key2 = StringKey("b")
            val keys = listOf(key1, key2)

            val data = PercentageChartData<Float>().apply {
                for (i in 0..100) {
                    val time = i * 100_000L
                    val angle = i * 0.1
                    addEntry(key1, PercentageEntry(time, (sin(angle) * 10 + 20).toFloat()))
                    addEntry(key2, PercentageEntry(time, (cos(angle) * 10 + 20).toFloat()))
                }
            }

            return ChartInfo(keys, ChartType.Percentage, data)
        }

        fun getEventsData(): ChartInfo<String> {
            val app1 = StringKey("app1")
            val app2 = StringKey("app2")
            val service1 = StringKey("service1")
            val keys = listOf(app1, app2, service1)

            val data = EventsChartData<String>().apply {
                addEntry(app1, EventEntry(500_000L, "Crash", ""))
                addEntry(app2, EventEntry(1_000_000L, "ANR", ""))
                addEntry(app2, EventEntry(1_500_000L, "Crash", ""))
                addEntry(service1, EventEntry(2_000_000L, "WTF", ""))
            }

            return ChartInfo(keys, ChartType.Events, data)
        }

        fun getStateData(): ChartInfo<String> {
            val key1 = StringKey("process1")
            val key2 = StringKey("process2")
            val keys = listOf(key1, key2)

            val data = StateChartData<String>().apply {
                addEntry(key1, StateEntry(0L, "Stopped", "Starting", ""))
                addEntry(key1, StateEntry(100_000L, "Starting", "Running", ""))
                addEntry(key1, StateEntry(500_000L, "Running", "Stopping", ""))
                addEntry(key1, StateEntry(600_000L, "Stopping", "Stopped", ""))

                addEntry(key2, StateEntry(200_000L, "Stopped", "Running", ""))
                addEntry(key2, StateEntry(800_000L, "Running", "Stopped", ""))
            }

            return ChartInfo(keys, ChartType.State, data)
        }

        fun getSingleStateData(): ChartInfo<String> {
            val key1 = StringKey("cpu1")
            val key2 = StringKey("cpu2")
            val keys = listOf(key1, key2)

            val data = SingleStateChartData<String>().apply {
                addEntry(key1, SingleStateEntry(0L, "Idle", ""))
                addEntry(key1, SingleStateEntry(100_000L, "Busy", ""))
                addEntry(key1, SingleStateEntry(500_000L, "Idle", ""))

                addEntry(key2, SingleStateEntry(0L, "Idle", ""))
                addEntry(key2, SingleStateEntry(200_000L, "Busy", ""))
                addEntry(key2, SingleStateEntry(400_000L, "Idle", ""))
            }

            return ChartInfo(keys, ChartType.SingleState, data)
        }

        fun getDurationData(): ChartInfo<String> {
            val key1 = StringKey("task1")
            val key2 = StringKey("task2")
            val keys = listOf(key1, key2)

            val data = DurationChartData<String>().apply {
                addEntry(key1, DurationEntry(100_000L, "Start", null, ""))
                addEntry(key1, DurationEntry(500_000L, null, "End", ""))

                addEntry(key2, DurationEntry(200_000L, "Start", null, ""))
                addEntry(key2, DurationEntry(400_000L, null, "End", ""))
            }

            return ChartInfo(keys, ChartType.Duration, data)
        }
    }
}