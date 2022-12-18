package io.github.seisuke.kemoji

import io.github.seisuke.kemoji.EmojiManager.getByUnicode
import io.github.seisuke.kemoji.EmojiManager.isEmoji
import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiManagerTest {

    @Test
    fun getAll() {
        val allEmoji = EmojiManager.getAll()
        assertEquals(1855, allEmoji.size)
    }

    @Test
    fun getForAlas() {
        val emoji = EmojiManager.getForAlias("wave")!!
        assertEquals("👋", emoji.emoji)
    }

    @Test
    fun getForTag_with_unknown_tag_returns_null() {
        val emojis = EmojiManager.getForTag("jkahsgdfjksghfjkshf")

        assertEquals(emptyList(), emojis)
    }

    @Test
    fun getForTag_returns_the_emojis_for_the_tag() {
        val emojis = EmojiManager.getForTag("happy")

        assertEquals(4, emojis.size)
        //TODO add alias test
    }

    @Test
    fun getByUnicode_returns_correct_emoji() {
        val wavingHand = "\uD83D\uDC4B"
        val emoji = getByUnicode(wavingHand) ?: throw RuntimeException()
        assertEquals(wavingHand, emoji.emoji)
        assertEquals("waving hand", emoji.description)
    }

    @Test
    fun isEmoji_with_startPos_for_an_emoji_and_other_chars_returns_exactly() {
        val text = "😀 test"
        val isEmoji = isEmoji(text, 0, 2)
        assertEquals(EmojiTrie.Matches.EXACTLY, isEmoji)
    }

    @Test
    fun isEmoji_with_startPos_for_an_fitzpatrick_emoji_and_other_chars_returns_exactly() {
        val text = "👋🏻 test"
        val isEmoji = isEmoji(text, 0, 2)
        assertEquals(EmojiTrie.Matches.EXACTLY, isEmoji)
    }
}
