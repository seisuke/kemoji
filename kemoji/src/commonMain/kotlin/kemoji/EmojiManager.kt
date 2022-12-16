package kemoji

object EmojiManager {

    private val emojiByAlias: Map<String, Emoji> by lazy {
        EmojiList.emojis.flatMap { emoji ->
            emoji.aliases.map { alias -> alias to emoji }
        }.toMap()
    }

    private val emojiByTag: Map<String, List<Emoji>> by lazy {
        EmojiList.emojis.flatMap { emoji ->
            emoji.tags.map { tag -> tag to emoji }
        }.groupBy(
            { (tag, _) -> tag },
            { (_, emoji) -> emoji },
        )
    }
    private val emojiTrie: EmojiTrie by lazy {
        EmojiTrie(EmojiList.emojis)
    }

    fun getAll(): List<Emoji> = EmojiList.emojis

    fun getForAlias(alias: String): Emoji? = emojiByAlias[alias]

    fun getForTag(tag: String): List<Emoji> = emojiByTag[tag] ?: emptyList()

    fun getByUnicode(unicode: String): Emoji? = emojiTrie.getEmoji(unicode)

    fun isEmoji(unicode: String): Boolean {
        val unicodeCandidate = EmojiParser.getNextUnicodeCandidate(unicode.toCharArray(), 0)
        return unicodeCandidate != null && unicodeCandidate.startIndex == 0 && unicodeCandidate.fitzpatrickEndIndex == unicode.length
    }

    fun isEmoji(unicode: String, startPos: Int, endPos: Int): EmojiTrie.Matches =
        emojiTrie.isEmoji(unicode, startPos, endPos)
}
