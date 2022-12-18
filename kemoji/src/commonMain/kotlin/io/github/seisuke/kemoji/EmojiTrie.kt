package io.github.seisuke.kemoji

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

    fun getEmoji(unicode: String): Emoji? = searchNode(root, unicode, true)?.emoji

    private tailrec fun searchNode(
        node: EmojiNode,
        pattern: String,
        indexCheck: Boolean = false,
        depth: Int = 0,
        fitzpatrickIndex: List<Int> = emptyList(),
        vs16Index: List<Int> = emptyList(),
    ): EmojiNode? {
        val head = pattern.firstOrNull() ?: return if (indexCheck) {
            node.indexCheck(fitzpatrickIndex, vs16Index)
        } else {
            node
        }
        return when {
            pattern.length == 1 && head == Fitzpatrick.FITZPATRICK_FIRST_HALF -> {
                //FIXME For return Matches.POSSIBLY in {@link com.github.seisuke.kemoji.EmojiTrie#isEmoji()},
                // this node can't indexCheck.
                return EmojiNode()
            }
            matchFitzpatrick(pattern) -> searchNode(
                node,
                pattern.drop(2),
                indexCheck,
                depth + 2,
                fitzpatrickIndex + listOf(depth),
                vs16Index,
            )
            head == VARIATION_SELECTOR_16 -> searchNode(
                node,
                pattern.drop(1),
                indexCheck,
                depth + 1,
                fitzpatrickIndex,
                vs16Index + listOf(depth),
            )
            else -> {
                val child = node.children[head] ?: return null
                searchNode(
                    child,
                    pattern.drop(1),
                    indexCheck,
                    depth + 1,
                    fitzpatrickIndex,
                    vs16Index,
                )
            }
        }
    }

    private fun matchFitzpatrick(pattern: String): Boolean =
        pattern.length >= 2 && Fitzpatrick.fitzpatrickRegex.matches(pattern.substring(0, 2))

    private fun EmojiNode.indexCheck(
        fitzpatrickIndex: List<Int>,
        vs16Index: List<Int>,
    ): EmojiNode? {
        val emoji = this.emoji ?: return this

        return if (indexCheck(fitzpatrickIndex, vs16Index, emoji)) {
            this
        } else {
            null
        }
    }

    private fun indexCheck(
        fitzpatrickIndex: List<Int>,
        vs16Index: List<Int>,
        emoji: Emoji
    ): Boolean {
        if (fitzpatrickIndex.isNotEmpty() && vs16Index.isNotEmpty()) {
            // for "man detective" group
            val manDetectiveGroupOffset = if (vs16Index.size < emoji.vs16Index.size) {
                emoji.vs16Index.size - vs16Index.size
            } else {
                0
            }
            val offsetedVs16Index = emoji.vs16Index.map { vs16 ->
                val offset = fitzpatrickIndex.filter { it < vs16 }.size * 2
                vs16 + offset - manDetectiveGroupOffset
            }
            if (fitzpatrickIndex != emoji.fitzpatrickIndex || !offsetedVs16Index.containsAll(vs16Index)) {
                return false
            }
        } else if (fitzpatrickIndex.isNotEmpty() && fitzpatrickIndex != emoji.fitzpatrickIndex) {
            // for "kiss: person, person" group
            val offsetedFitzpatrickIndex = emoji.fitzpatrickIndex.map { fitzpatrick ->
                val offset = emoji.vs16Index.filter { it < fitzpatrick }.size
                fitzpatrick - offset
            }
            if (offsetedFitzpatrickIndex != fitzpatrickIndex) {
                return false
            }
        } else if (vs16Index.isNotEmpty() && vs16Index != emoji.vs16Index) {
            return false
        }
        return true
    }

    enum class Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE
    }

    internal class EmojiNode {
        val children: MutableMap<Char, EmojiNode> = mutableMapOf()
        var emoji: Emoji? = null
    }

    companion object {
        val VARIATION_SELECTOR_16 = Char(65039)
    }
}



