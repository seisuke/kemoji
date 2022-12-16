package kemoji

data class Emoji(
    val emoji: String,
    val description: String,
    val category: String,
    val aliases: List<String>,
    val tags: List<String>,
    val unicodeVersion: UnicodeVersion,
    val iosVersion: Float,
    val supportsFitzpatrick: Boolean = false,
)

sealed class UnicodeVersion {
    object Empty : UnicodeVersion()
    class Version(val version: Float) : UnicodeVersion()
}
