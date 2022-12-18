package io.github.seisuke.kemoji

import io.github.seisuke.emojilistgenerator.EmojiListGenerator
import io.github.seisuke.emojilistgenerator.EmojiTestReader
import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiListGeneratorTest {

    @Test
    fun emojiListTest() {
        val emojiList = EmojiListGenerator.fileLoader()
        assertEquals(1855, emojiList.size)
    }

    @Test
    fun generateEmojiList() {
        //TODO move to gradle
        EmojiListGenerator.generateEmojiLists()
    }

    @Test
    fun groupByUnicodeTest() {
        val emojiTestList = EmojiTestReader.loadEmojiTest()
        val groupList = EmojiTestReader.groupByRawUnicode(emojiTestList)
        //assertEquals(group["🖐"]!!.size, 6) // 🖐 is unique

        val group = groupList["🤚"]!!
        assertEquals(6, group.size)
    }
}
