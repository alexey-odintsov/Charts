package alexey.odintsov.charts

actual fun formatFloat(value: Float, decimals: Int, thousandsSeparator: Boolean): String {
    return if (thousandsSeparator) {
        "%,.${decimals}f".format(value)
    } else {
        "%.${decimals}f".format(value)
    }
}