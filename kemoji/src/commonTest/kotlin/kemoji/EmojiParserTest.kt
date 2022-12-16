package kemoji

import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiParserTest {

    @Test
    fun parseToAliases_replaces_the_emojis_by_one_of_their_aliases() {
        val text = "An 😀awesome 😃string with a few 😉emojis!"
        val result = EmojiParser.parseToAliases(text)

        assertEquals(
            "An :grinning:awesome :smiley:string with a few :wink:emojis!",
            result
        )
    }

    @Test
    fun parseToAliases_replaces_the_fitzpatrick_emoji_by_one_of_their_aliases() {
        val text = "fitzpatrick 👋🏻 emoji"
        val result = EmojiParser.parseToAliases(text)

        assertEquals(
            "fitzpatrick :wave|type_1_2: emoji",
            result
        )
    }
}
