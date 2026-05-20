package alexey.odintsov.charts.model

internal enum class SeriesType {
    ByValue,
    ByNumber,
}

enum class ChartType {
    Percentage,
    MinMax,
    Events,
    State,
    SingleState,
    Duration,
}