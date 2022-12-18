package io.github.seisuke.kemoji

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
    fun parseToAliases_replaces_the_incomplete_emoji_by_one_of_their_aliases() {
        val text = "incomplete \uD83E\uD83C\uDFFB\u200D\uDEF2\uD83C\uDFFC"
        val result = EmojiParser.parseToAliases(text)

        assertEquals(
            "incomplete \uD83E\uD83C\uDFFB\u200D\uDEF2\uD83C\uDFFC",
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

    @Test
    fun parseToAliases_replaces_the_minimally_qualified_emoji_by_one_of_their_aliases() {
        val text = "⛑️ emoji ⛑️"
        val result = EmojiParser.parseToAliases(text)
        assertEquals(
            ":rescue_worker_helmet: emoji :rescue_worker_helmet:",
            result
        )
    }

    @Test
    fun parseToAliases_replaces_the_multiple_vs16_emoji_by_one_of_their_aliases() {
        val text = "👁️‍🗨️ emoji 👁️‍🗨️"
        val result = EmojiParser.parseToAliases(text)
        assertEquals(
            ":eye_speech_bubble: emoji :eye_speech_bubble:",
            result
        )
    }

    @Test
    fun parseToAliases_replaces_the_multiple_fitzpatrick_emoji_by_one_of_their_aliases() {
        val text = "🫱🏻‍🫲🏼 emoji 🫱🏿‍🫲🏻"
        val result = EmojiParser.parseToAliases(text)
        assertEquals(
            ":handshake|type_1_2|type_3: emoji :handshake|type_6|type_1_2:",
            result
        )
    }

    @Test
    fun parseToAliases_replaces_the_multiple_fitzpatrick_emoji_with_remove_option_by_one_of_their_aliases() {
        val text = "🫱🏻‍🫲🏼 emoji 🫱🏿‍🫲🏻"
        val result = EmojiParser.parseToAliases(text, FitzpatrickAction.REMOVE)
        assertEquals(
            ":handshake: emoji :handshake:",
            result
        )
    }

    @Test
    fun parseToAliases_replaces_the_multiple_fitzpatrick_emoji_with_ignore_option_by_one_of_their_aliases() {
        val text = "🫱🏻‍🫲🏼 emoji 🫱🏿‍🫲🏻"
        val result = EmojiParser.parseToAliases(text, FitzpatrickAction.IGNORE)
        assertEquals(
            ":handshake:\uD83C\uDFFB\uD83C\uDFFC emoji :handshake:\uD83C\uDFFF\uD83C\uDFFB",
            result
        )
    }

    @Test
    fun removeAllEmojis_removes_all_the_emojis_from_the_string() {
        val text = "fitzpatrick 👋🏻 emoji ⛑️ helmet 👁️‍🗨️ eye_speech_bubble"
        val result = EmojiParser.removeAllEmojis(text)
        assertEquals(
            "fitzpatrick  emoji  helmet  eye_speech_bubble",
            result
        )
    }

    @Test
    fun parseToSpanList_with_fitzpatrick_emoji() {
        val text = "fitzpatrick 👈🏻 emoji ⛑️ helmet"
        val result = EmojiParser.parseToSpanList(text) { uc ->
            uc.emoji // emoji removed fitzpatrick, off course uc has fitzpatrick data.
        }.joinToString("") {
            when (it) {
                is TextOrSpan.Text -> it.text
                is TextOrSpan.Span -> it.box.emoji
            }
        }
        assertEquals(
            "fitzpatrick 👈 emoji ⛑ helmet", //removed fitzpatrick
            result
        )
    }

}
