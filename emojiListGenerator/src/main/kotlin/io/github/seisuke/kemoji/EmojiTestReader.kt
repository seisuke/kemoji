package io.github.seisuke.kemoji

object EmojiTestReader {

    private val commentRegexp = Regex("^[#\\s]")
    private val skipStatusRegexp = Regex("(unqualified|component)")

    fun loadEmojiTest(): List<EmojiTest> {
        val stream = this::class.java.getResourceAsStream("/unicode-emoji-test.txt")
        val emojiTests: MutableList<EmojiTest> = mutableListOf()
        stream.bufferedReader().forEachLine { line ->
            if (line.isNotBlank() && !commentRegexp.containsMatchIn(line)) {
                if (!skipStatusRegexp.containsMatchIn(line)) {
                    val splittedLine = line.split(";")
                    val unicode = splittedLine[0].trim()
                    val description = splittedLine[1].split(Regex("E\\d+\\.\\d+")).last().trim()
                    val emoji = convertToEmoji(unicode)
                    emojiTests.add(
                        EmojiTest(emoji, description)
                    )
                }
            }
        }
        return emojiTests
    }

    fun groupByRawUnicode(emojiTests: List<EmojiTest>): Map<String, List<EmojiTest>> {
        return emojiTests.groupBy {test ->
            test.emoji.replace(fitzpatrickRegex, "")
                .replace(VARIATION_SELECTOR_16, "")
        }.filter { map -> map.value.size > 2 }
    }

    private fun convertToEmoji(input: String): String {
        return input.split(" ").joinToString("") { codePointAsString ->
            val codePoint = convertFromCodepoint(codePointAsString)
            Character.toChars(codePoint).joinToString("")
        }
    }

    private fun convertFromCodepoint(emojiCodepointAsString: String): Int {
        return emojiCodepointAsString.toInt(16)
    }

    data class EmojiTest(
        val emoji: String,
        val description: String,
    )
}
