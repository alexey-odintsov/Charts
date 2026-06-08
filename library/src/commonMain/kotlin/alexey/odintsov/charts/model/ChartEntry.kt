package alexey.odintsov.charts.model

import alexey.odintsov.charts.formatFloat

interface ChartEntry<out T> {
    val timestamp: Long
    val data: T?

    fun getText(customKeys: Map<String, String>? = null): String
}

data class PercentageEntry<T>(
    override val timestamp: Long,
    val value: Float,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        return formatFloat(value, 2)
    }
}

data class MinMaxEntry<T>(
    override val timestamp: Long,
    val value: Float,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        return formatFloat(value, 2)
    }
}

data class DurationEntry<T>(
    override val timestamp: Long,
    val begin: String?,
    val end: String?,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        val b = customKeys?.get(begin)?.let { "$it ($begin)" } ?: begin
        val e = customKeys?.get(end)?.let { "$it ($end)" } ?: end
        return if (b != null && e != null) "$b -> $e" else b ?: (e ?: "")
    }
}

data class EventEntry<T>(
    override val timestamp: Long,
    val event: String,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        return customKeys?.get(event)?.let { "$it ($event)" } ?: event
    }
}

data class StateEntry<T>(
    override val timestamp: Long,
    val oldState: String,
    val newState: String,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        val old = customKeys?.get(oldState)?.let { "$it ($oldState)" } ?: oldState
        val new = customKeys?.get(newState)?.let { "$it ($newState)" } ?: newState
        return "$old -> $new"
    }
}

data class SingleStateEntry<T>(
    override val timestamp: Long,
    val state: String,
    override val data: T? = null
) : ChartEntry<T> {
    override fun getText(customKeys: Map<String, String>?): String {
        return customKeys?.get(state)?.let { "$it ($state)" } ?: state
    }
}
