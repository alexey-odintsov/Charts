package alexey.odintsov.kmp.charts.testapp

import alexey.odintsov.kmp.charts.model.ChartType
import alexey.odintsov.kmp.charts.model.MinMaxChartData
import alexey.odintsov.kmp.charts.model.MinMaxEntry
import alexey.odintsov.kmp.charts.model.StringKey
import alexey.odintsov.kmp.charts.model.TimeFrame
import alexey.odintsov.kmp.charts.ui.Chart
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import charts.test_app.generated.resources.Res
import charts.test_app.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val entries = remember {
            val key = StringKey("a")
            val key2 = StringKey("b")
            MinMaxChartData<Float>().also {
                it.addEntry(key, MinMaxEntry(0, 10f))
                it.addEntry(key, MinMaxEntry(1, 15f))
                it.addEntry(key, MinMaxEntry(3, 5f))
                it.addEntry(key2, MinMaxEntry(2, 2f))
                it.addEntry(key2, MinMaxEntry(4, 20f))
                it.addEntry(key2, MinMaxEntry(10, 6f))
            }
        }

        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            Chart(
                modifier = Modifier.fillMaxWidth().height(300.dp),
                type = ChartType.MinMax,
                totalTime = TimeFrame(0, 10),
                timeFrame = TimeFrame(0, 10),
                entries = entries,
            )
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}