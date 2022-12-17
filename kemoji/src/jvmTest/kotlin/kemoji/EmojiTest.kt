package kemoji

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.assertEquals
import kotlin.test.assertNotNull


class EmojiTest {

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("emojis")
    fun emojisTest(emoji: String, description: String) {
        assertNotNull(
            EmojiManager.getByUnicode(emoji),
            "Asserting for emoji: $emoji $description"
        )
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("emojis")
    fun emojisAliasTest(unicode: String, description: String) {
        val emoji = EmojiManager.getByUnicode(unicode)!!
        val alias = EmojiParser.emojiToAlias(emoji, null)

        val text = " $unicode "
        val result = EmojiParser.parseToAliases(text)
        if (!result.contains("type_")) {
            assertEquals(
                " $alias ",
                result
            )
        }
    }

    private object EmojiTestReader {

        private val comment_regexp = Regex("^[#\\s]")
        private val skip_status_regexp = Regex("(unqualified|component)")

        fun loadEmojiTest(): List<Arguments> {
            val stream = this::class.java.getResourceAsStream("/unicode-emoji-test.txt")
            val emojiList: MutableList<Arguments> = mutableListOf()
            stream.bufferedReader().forEachLine { line ->
                if (line.isNotBlank() && !comment_regexp.containsMatchIn(line)) {
                    if (!skip_status_regexp.containsMatchIn(line)) {
                        val splittedLine = line.split(";")
                        val unicode = splittedLine[0].trim()
                        val description = splittedLine[1].split(Regex("E\\d+\\.\\d+")).last().trim()
                        val emoji = convertToEmoji(unicode)
                        emojiList.add(
                            Arguments.of(emoji, description)
                        )
                    }
                }
            }
            return emojiList
        }

        private fun convertToEmoji(input: String): String {
            return input.split(" ").joinToString("") { codePointAsString ->
                val codePoint = convertFromCodepoint(codePointAsString)
                Character.toChars(codePoint).joinToString("")
            }
        }

        fun convertFromCodepoint(emojiCodepointAsString: String): Int {
            return emojiCodepointAsString.toInt(16)
        }
    }

    companion object {
        @JvmStatic
        fun emojis(): List<Arguments> = EmojiTestReader.loadEmojiTest()
    }
}
