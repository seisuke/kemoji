package io.github.seisuke.kemoji

sealed class TextOrSpan<out T> {
    data class Text(val text: String): TextOrSpan<Nothing>()
    data class Span<T>(val box: T): TextOrSpan<T>()
}
