package alexey.odintsov.charts.ui

import alexey.odintsov.charts.model.ChartEntry
import alexey.odintsov.charts.model.ChartKey
import alexey.odintsov.charts.model.ChartType
import alexey.odintsov.charts.model.TimeFrame
import alexey.odintsov.charts.model.ChartData
import alexey.odintsov.charts.model.DurationChartData
import alexey.odintsov.charts.model.EventsChartData
import alexey.odintsov.charts.model.MinMaxChartData
import alexey.odintsov.charts.model.PercentageChartData
import alexey.odintsov.charts.model.SingleStateChartData
import alexey.odintsov.charts.model.StateChartData
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp


/**
 * A highly customizable chart component for displaying various types of data over time.
 *
 * @param T The type of the data associated with each chart entry.
 * @param modifier The modifier to be applied to the chart's container.
 * @param style The visual style configuration for the chart, including colors and text styles.
 * @param totalTime The full time range (min and max timestamps) that the chart can potentially display.
 * @param timeFrame The currently visible time range on the chart.
 * @param entries The data entries to be rendered in the chart.
 * @param onDragged A callback invoked when the user drags the chart, providing the drag amount in time units.
 * @param type The [ChartType] specifying how the data should be visualized (e.g., Percentage, MinMax, Events).
 * @param labelsCount The target number of labels to display on the Y-axis (primarily for numeric charts).
 * @param labelsPostfix A string to append to the end of Y-axis labels (e.g., units like "%" or "ms").
 * @param highlightedKey An optional [ChartKey] to highlight a specific data series.
 * @param selectedEntry The entry that is currently selected in the chart.
 * @param hoveredEntry The entry that is currently being hovered over by the pointer.
 * @param onEntrySelected A callback invoked when a chart entry is clicked or tapped.
 * @param onEntryHovered A callback invoked when the hover state of an entry changes.
 * @param customKeys An optional map for translating internal keys or values (like state IDs) into human-readable labels.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> Chart(
    modifier: Modifier,
    style: ChartStyle = ChartStyle.Default,
    totalTime: TimeFrame, // min .. max timeStamps
    timeFrame: TimeFrame,
    entries: ChartData<T>?,
    onDragged: ((Float) -> Unit)? = null,
    type: ChartType,
    labelsCount: Int = 11,
    labelsPostfix: String = "",
    highlightedKey: ChartKey? = null,
    selectedEntry: ChartEntry<T>? = null,
    hoveredEntry: ChartEntry<T>? = null,
    onEntrySelected: ((ChartEntry<T>) -> Unit)? = null,
    onEntryHovered: ((ChartEntry<T>?) -> Unit)? = null,
    customKeys: Map<String, String>? = null,
) {
    var usSize by remember { mutableStateOf(1f) }
    val textMeasurer = rememberTextMeasurer()
    val positionCache = remember(entries, type) { PositionCache<T>() }
    var localCursorPosition by remember { mutableStateOf(Offset.Zero) }
    var isCursorInsideChart by remember { mutableStateOf(false) }

    Box {
        Spacer(
            modifier = modifier.fillMaxSize().background(style.backgroundColor).clipToBounds()
                .onSizeChanged { size ->
                    usSize = size.width.toFloat() / timeFrame.duration
                }
                .pointerInput(onEntryHovered, entries, timeFrame) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            when (event.type) {
                                PointerEventType.Enter -> isCursorInsideChart = true
                                PointerEventType.Exit -> {
                                    isCursorInsideChart = false
                                    onEntryHovered?.invoke(null)
                                }

                                PointerEventType.Move -> {
                                    localCursorPosition = event.changes.first().position
                                    val hoveredEvent =
                                        positionCache.getNearestEntry(localCursorPosition)
                                    onEntryHovered?.invoke(hoveredEvent)
                                }
                            }
                        }
                    }
                }
                .pointerInput("chart-selection", entries, timeFrame) {
                    detectTapGestures(
                        onPress = { offset ->
                            val selectedEvent = positionCache.getNearestEntry(offset)
                            if (selectedEvent != null) {
                                onEntrySelected?.invoke(selectedEvent)
                            }
                        })
                }.pointerInput("chart-dragging") {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        val dragUs = -dragAmount.x / usSize
                        onDragged?.invoke(dragUs)
                    }
                }.drawWithCache {
                    onDrawBehind {
                        positionCache.clear()
                        renderCenterLine()
                        if (entries == null || entries.isEmpty()) {
                            renderEmptyMessage(textMeasurer, style)
                            return@onDrawBehind
                        }
                        val labelsSize = if (entries.getLabels()
                                .isNotEmpty()
                        ) entries.getLabels().size else labelsCount
                        renderSeries(type, labelsSize, style)
                        renderEntries(
                            type,
                            entries,
                            timeFrame,
                            style,
                            highlightedKey,
                            labelsSize,
                            selectedEntry,
                            hoveredEntry,
                            positionCache,
                        )
                        renderLabels(
                            type,
                            labelsSize,
                            textMeasurer,
                            style,
                            entries,
                            labelsPostfix,
                            customKeys
                        )
                    }
                })

        if (hoveredEntry != null && isCursorInsideChart) {
            val density = LocalDensity.current
            val xDp = with(density) { localCursorPosition.x.toDp() }
            val yDp = with(density) { localCursorPosition.y.toDp() }
            Text(
                text = hoveredEntry.getText(customKeys),
                color = if (style == ChartStyle.Default) Color.Black else Color.White,
                modifier = Modifier.absoluteOffset(xDp, yDp - 20.dp)
                    .background(if (style == ChartStyle.Default) Color.White else Color.Black)
                    .padding(horizontal = 2.dp)
            )
        }
    }
}

private fun <T> DrawScope.renderLabels(
    type: ChartType,
    labelsSize: Int,
    textMeasurer: TextMeasurer,
    style: ChartStyle,
    entries: ChartData<T>,
    labelsPostfix: String,
    customKeys: Map<String, String>? = null,
) {
    when (type) {
        ChartType.Percentage -> renderLabelsForValue(
            getSteps(0f, 100f, labelsSize),
            textMeasurer,
            style.labelTextStyle,
            "%",
            style.verticalPadding.toPx(),
        )

        ChartType.MinMax -> renderLabelsForValue(
            getSteps(
                (entries as MinMaxChartData).getMinValue(),
                (entries as MinMaxChartData).getMaxValue(),
                labelsSize
            ),
            textMeasurer,
            style.labelTextStyle,
            labelsPostfix,
            style.verticalPadding.toPx(),
        )

        ChartType.Events, ChartType.State, ChartType.SingleState, ChartType.Duration -> renderLabels(
            entries.getLabels(),
            textMeasurer,
            style.labelTextStyle,
            labelsPostfix,
            style.verticalPadding.toPx(),
            customKeys,
        )
    }
}

private fun DrawScope.renderCenterLine() {
    drawLine(
        Color.LightGray, Offset(size.center.x, 0f), Offset(size.center.x, size.height), alpha = 0.5f
    )
}

private fun <T> DrawScope.renderEntries(
    type: ChartType,
    entries: ChartData<T>,
    timeFrame: TimeFrame,
    style: ChartStyle,
    highlightedKey: ChartKey?,
    labelsSize: Int,
    selectedEntry: ChartEntry<T>?,
    hoveredEntry: ChartEntry<T>?,
    positionCache: PositionCache<T>,
) {
    when (type) {
        ChartType.Events -> renderEvents(
            entries as EventsChartData,
            timeFrame,
            style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.Percentage -> renderPercentageLines(
            entries as PercentageChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.MinMax -> renderMinMaxLines(
            entries as MinMaxChartData,
            labelsSize,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.State -> renderStateLines(
            entries as StateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.SingleState -> renderSingleStateLines(
            entries as SingleStateChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )

        ChartType.Duration -> renderDurationLines(
            entries as DurationChartData,
            timeFrame,
            style = style,
            highlightedKey = highlightedKey,
            selectedEntry = selectedEntry,
            hoveredEntry = hoveredEntry,
            positionCache,
        )
    }
}

private fun DrawScope.renderSeries(
    type: ChartType, labelsSize: Int, style: ChartStyle
) {
    when (type) {
        ChartType.Percentage, ChartType.MinMax -> renderSeriesByValue(
            labelsSize, style.seriesColor, style.verticalPadding.toPx()
        )

        else -> renderSeries(
            labelsSize, style.seriesColor, style.verticalPadding.toPx()
        )

    }
}

