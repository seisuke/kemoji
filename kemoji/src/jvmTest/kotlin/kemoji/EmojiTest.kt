package kemoji

import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import kotlin.test.DefaultAsserter.assertTrue


class EmojiTest {

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("emojis")
    fun emojisTest(emoji: String, description: String) {
        val rawUnicode = emoji.replace(Fitzpatrick.TYPE_1_2.unicode, "")
            .replace(Fitzpatrick.TYPE_3.unicode, "")
            .replace(Fitzpatrick.TYPE_4.unicode, "")
            .replace(Fitzpatrick.TYPE_5.unicode, "")
            .replace(Fitzpatrick.TYPE_6.unicode, "")
            .replace(Char(65039).toString(), "")
        
        // can't pass 160 tests
        // 1FAF1 1F3FB 200D 1FAF2 1F3FC handshake with skin tone
        // 1F469 1F3FB 200D 1F91D 200D 1F469 1F3FC women|men holding hands with skin tone
        // 1F9D1 1F3FB 200D 2764 200D 1F48B 200D 1F9D1 1F3FC kiss: person, person with skin tone
        // 1F9D1 1F3FB 200D 2764 FE0F 200D 1F9D1 1F3FC  couple with heart: person, person with skin tone
        assertTrue("Asserting for emoji: $emoji $description", EmojiManager.isEmoji(rawUnicode));
    }

    private object EmojiTestReader {

        private val comment_regexp = Regex("^[#\\s]")
        private val skip_status_regexp = Regex("(unqualified|component)")

        fun getEmojiList(): List<Arguments> {
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
        fun emojis(): List<Arguments> = EmojiTestReader.getEmojiList()
    }
}
