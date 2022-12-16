import kotlin.test.Test
import kotlin.test.assertEquals

class EmojiListGeneratorTest {

    @Test
    fun emojiListTest() {
        val emojiList = EmojiListGenerator.fileLoader()
        assertEquals(emojiList.size, 1855)
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
        assertEquals(group.size, 6)
    }
}
