import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiListGeneratorTest {

    @Test
    fun emojiListTest() {
        val emojiList = EmojiListGenerator.fileLoader()
        assertEquals(emojiList.size, 1849)
    }

    /*
    @Test
    fun generateEmojiList() {
        //TODO move to gradle
        EmojiListGenerator.generateEmojiLists()
    }
     */

    @Test
    fun groupByUnicodeTest() {
        val emojiTestList = EmojiTestReader.getEmojiList()
        val group = EmojiTestReader.groupByRawUnicode(emojiTestList)
        assertEquals(group["👋"]!!.size, 6)
    }
}
