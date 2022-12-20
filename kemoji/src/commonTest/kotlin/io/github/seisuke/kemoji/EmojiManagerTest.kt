package io.github.seisuke.kemoji

import io.github.seisuke.kemoji.EmojiManager.isEmoji
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
        val emoji = EmojiManager.getByUnicode(wavingHand) ?: throw RuntimeException()
        assertEquals(wavingHand, emoji.emoji)
        assertEquals("waving hand", emoji.description)
    }

    @Test
    fun isEmoji_for_an_emoji_and_other_chars_returns_true() {
        val text = "👋🏻"
        val isEmoji = isEmoji(text)
        assertTrue(isEmoji)
    }

    @Test
    fun containsEmoji_returns_true() {
        val text = "test 👋🏻 test"
        val containsEmoji = EmojiManager.containsEmoji(text)
        assertTrue(containsEmoji)
    }

    @Test
    fun containsEmoji_returns_false() {
        val text = "test test"
        val containsEmoji = EmojiManager.containsEmoji(text)
        assertFalse(containsEmoji)
    }

    @Test
    fun containsEmoji_with_blank_text() {
        val text = ""
        val containsEmoji = EmojiManager.containsEmoji(text)
        assertFalse(containsEmoji)
    }

    @Test
    fun isOnlyEmoji_returns_true() {
        val text = "👋🏻👋⛑️"
        val containsEmoji = EmojiManager.isOnlyEmojis(text)
        assertTrue(containsEmoji)
    }

    @Test
    fun isOnlyEmoji_returns_false() {
        val text = "👋\u200D"
        val containsEmoji = EmojiManager.isOnlyEmojis(text)
        assertFalse(containsEmoji)
    }

    @Test
    fun isOnlyEmoji_with_blank_text() {
        val text = ""
        val containsEmoji = EmojiManager.isOnlyEmojis(text)
        assertFalse(containsEmoji)
    }
}
