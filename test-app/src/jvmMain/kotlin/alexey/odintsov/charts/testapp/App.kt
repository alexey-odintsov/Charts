package alexey.odintsov.charts.testapp

import alexey.odintsov.charts.model.ChartEntry
import alexey.odintsov.charts.model.ChartKey
import alexey.odintsov.charts.model.TimeFrame
import alexey.odintsov.charts.ui.Chart
import alexey.odintsov.charts.ui.ChartStyle
import alexey.odintsov.charts.ui.TimeRuler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
@Preview
fun App() {
    var timeTotal by mutableStateOf(TimeFrame(0, 10000000))
    var timeFrame by mutableStateOf(TimeFrame(0, 12000000))
    var highlightedKey by mutableStateOf<ChartKey?>(null)
    val chartsInfo = remember {
        listOf(
            ChartInfo.getMinMaxData(),
            ChartInfo.getPercentageData(),
            ChartInfo.getEventsData(),
            ChartInfo.getStateData(),
            ChartInfo.getSingleStateData(),
            ChartInfo.getSingleStateDataCustomKeys(),
            ChartInfo.getDurationData(),
        )
    }

    MaterialTheme {
        var hoveredEntry by remember { mutableStateOf<ChartEntry<Any>?>(null) }
        var selectedEntry by remember { mutableStateOf<ChartEntry<Any>?>(null) }
        var isDarkChart by remember { mutableStateOf(false) }
        var index by remember { mutableStateOf(0) }

        Column(
            modifier = Modifier.safeContentPadding().fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                var expanded by remember { mutableStateOf(false) }
                Box {
                    Button(onClick = { expanded = true }) {
                        Text("Chart: ${chartsInfo[index].title}")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        chartsInfo.forEachIndexed { i, info ->
                            DropdownMenuItem(
                                text = { Text(info.title) },
                                onClick = {
                                    index = i
                                    expanded = false
                                    highlightedKey = null
                                    hoveredEntry = null
                                    selectedEntry = null
                                }
                            )
                        }
                    }
                }
                Button(onClick = { isDarkChart = !isDarkChart }) {
                    Text("Toggle style")
                }
                Button(onClick = { timeFrame = timeFrame.zoom(true) }) {
                    Text("ZoomIn")
                }
                Button(onClick = { timeFrame = timeFrame.zoom(false) }) {
                    Text("ZoomOut")
                }
                Button(onClick = { timeFrame = timeTotal }) {
                    Text("Fit")
                }
            }
            TimeRuler(
                modifier = Modifier.fillMaxWidth().height(40.dp),
                timeTotal = timeTotal,
                timeFrame = timeFrame,
            )
            Chart<Any>(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                type = chartsInfo[index].chartType,
                style = if (isDarkChart) ChartStyle.Dark else ChartStyle.Default,
                totalTime = timeTotal,
                timeFrame = timeFrame,
                entries = chartsInfo[index].entries,
                hoveredEntry = hoveredEntry,
                selectedEntry = selectedEntry,
                highlightedKey = highlightedKey,
                onDragged = {
                    timeFrame = timeFrame.move(it.toLong())
                },
                onEntryHovered = {
                    hoveredEntry = it
                },
                onEntrySelected = {
                    selectedEntry = it
                },
                customKeys = chartsInfo[index].customKeys)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("Toggle Key: ")
                chartsInfo[index].keys.forEach { key ->
                    KeyToggleButton(highlightedKey, key) {
                        highlightedKey = if (highlightedKey == key) {
                            null
                        } else {
                            key
                        }
                    }
                }
            }
            Text("Selected entry: ${selectedEntry?.getText(chartsInfo[index].customKeys) ?: "none"}")
        }
    }
}

@Composable
private fun KeyToggleButton(
    highlightedKey: ChartKey?,
    key: ChartKey,
    onToggle: (ChartKey) -> Unit
) {
    Button(
        colors = if (highlightedKey == key) ButtonDefaults.filledTonalButtonColors() else ButtonDefaults.buttonColors(),
        onClick = { onToggle(key) }) {
        Text(
            text = key.key,
            modifier = Modifier.padding(horizontal = 10.dp),
            fontWeight = if (highlightedKey == key) FontWeight.Bold else FontWeight.Normal
        )
    }
}