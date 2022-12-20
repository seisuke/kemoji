package io.github.seisuke.kemoji

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

    fun getAll(): List<Emoji> = EmojiList.emojis

    fun getForAlias(alias: String): Emoji? = emojiByAlias[alias] //TODO trim alias

    fun getForTag(tag: String): List<Emoji> = emojiByTag[tag] ?: emptyList()

    fun getByUnicode(unicode: String): Emoji? = EmojiTrieStore.getEmoji(unicode)

    fun isEmoji(unicode: String): Boolean =
        (EmojiTrieStore.isEmoji(unicode) == EmojiTrie.Matches.EXACTLY)

    fun containsEmoji(text: String): Boolean {
        return EmojiParser.getNextUnicodeCandidate(text, 0) != null
    }

    fun isOnlyEmojis(text: String): Boolean {
        return text.isNotBlank() && EmojiParser.removeAllEmojis(text).isEmpty()
    }
}
