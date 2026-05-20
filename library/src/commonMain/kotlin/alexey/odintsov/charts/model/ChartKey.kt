package alexey.odintsov.charts.model

interface ChartKey {
    val key: String
}

data class StringKey(
    override val key: String
) : ChartKey