# Charts

A Compose Multiplatform charts library for Android and Desktop.

## Features

- Built with **Compose Multiplatform**.
- Supports multiple chart types:
    - **Percentage**: Percentage-based data visualization.
    - **MinMax**: Min/Max value range over time.
    - **Events**: Discrete events visualization.
    - **State** & **SingleState**: State changes tracking.
    - **Duration**: Duration-based data.
- Interactive: supports dragging, hovering, and selection.
- Customizable styles.

## Installation

Add the dependency to your `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.alexey-odintsov:charts:0.0.3")
        }
    }
}
```

## Usage

Basic example of using the `Chart` composable:

```kotlin
Chart(
    modifier = Modifier.fillMaxWidth().height(300.dp),
    type = ChartType.Percentage,
    entries = chartData,
    timeFrame = currentTimeFrame,
    totalTime = totalTimeFrame,
    onDragged = { delta -> /* handle drag */ },
    onEntrySelected = { entry -> /* handle selection */ }
)
```

## Development

The project contains a `test-app` module that demonstrates various chart types.

### Running the Test App

To run the desktop version of the test app:

```bash
./gradlew :test-app:run
```

## License

This project is licensed under the MIT License.
