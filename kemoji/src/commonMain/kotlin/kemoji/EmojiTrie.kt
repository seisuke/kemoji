package kemoji

class EmojiTrie(
    val emojis: List<Emoji>
) {
    private val root: EmojiNode = EmojiNode()
    init {
        emojis.forEach { emoji ->
            val node = emoji.emoji.toCharArray().fold(root) { parent, c ->
                parent.children.getOrPut(c) { EmojiNode() }
            }
            node.emoji = emoji
        }
    }

    fun isEmoji(unicode: String, start: Int, end: Int): Matches {
        if (start < 0 || start > end || end > unicode.length) {
            throw IndexOutOfBoundsException(
                "start $start, end $end, length ${unicode.length}"
            )
        }
        val node = searchNode(root, unicode.substring(start, end))
        return if (node == null) {
            Matches.IMPOSSIBLE
        } else if (node.emoji == null) {
            Matches.POSSIBLY
        } else {
            Matches.EXACTLY
        }
    }

    fun getEmoji(unicode: String): Emoji? = searchNode(root, unicode)?.emoji

    private tailrec fun searchNode(node: EmojiNode, pattern: String): EmojiNode? {
        val head = pattern.firstOrNull() ?: return node
        val child = node.children[head] ?: return null
        val tail = pattern.drop(1)
        return searchNode(child, tail)
    }

    enum class Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE
    }

    internal class EmojiNode {
        val children: MutableMap<Char, EmojiNode> = mutableMapOf()
        var emoji: Emoji? = null
    }

}



