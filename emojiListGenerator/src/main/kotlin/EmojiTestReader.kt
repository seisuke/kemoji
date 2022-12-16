object EmojiTestReader {

    private val comment_regexp = Regex("^[#\\s]")
    private val skip_status_regexp = Regex("(unqualified|component)")

    fun getEmojiList(): List<EmojiTest> {
        val stream = this::class.java.getResourceAsStream("unicode-emoji-test.txt")
        val emojiTests: MutableList<EmojiTest> = mutableListOf()
        stream.bufferedReader().forEachLine { line ->
            if (line.isNotBlank() && !comment_regexp.containsMatchIn(line)) {
                if (!skip_status_regexp.containsMatchIn(line)) {
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
            val rawUnicode = test.emoji.replace(Fitzpatrick.TYPE_1_2.unicode, "")
                .replace(Fitzpatrick.TYPE_3.unicode, "")
                .replace(Fitzpatrick.TYPE_4.unicode, "")
                .replace(Fitzpatrick.TYPE_5.unicode, "")
                .replace(Fitzpatrick.TYPE_6.unicode, "")
                .replace(Char(65039).toString(), "")
            rawUnicode
        }
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
