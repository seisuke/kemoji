package io.github.seisuke.kemoji


object EmojiParser {

    fun parseToAliases(
        input: String,
        fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
    ): String {
        val emojiTransformer: (UnicodeCandidate) -> String = { uc ->
            emojiToAlias(uc.emoji, uc.fitzpatrickList, fitzpatrickAction)
        }
        return parseFromUnicode(input, emojiTransformer)
    }

    fun removeAllEmojis(input: String): String = parseFromUnicode(input) { "" }

    fun getNextUnicodeCandidate(text: String, startPos: Int): UnicodeCandidate? {
        val (index, endPos) = (startPos until text.length).asSequence().map { index ->
            index to getEmojiEndPos(text, index)
        }.firstOrNull { (_, endPos) ->
            endPos != -1
        } ?: return null

        val subString = text.substring(index, endPos)
        val emoji = EmojiTrieStore.getEmoji(subString) ?: return null
        val fitzpatrickList = Fitzpatrick.fitzpatrickRegex.findAll(subString)
            .mapNotNull { result ->
                Fitzpatrick.fitzpatrickFromUnicode(result.value)
            }.toList()

        return UnicodeCandidate(
            emoji,
            index,
            endPos,
            fitzpatrickList,
        )
    }

    fun <T> parseToSpanList(
        input: String,
        spanGenerator: (UnicodeCandidate) -> T
    ): List<TextOrSpan<T>> {
        val result = mutableListOf<TextOrSpan<T>>()
        val lastIndex = getUnicodeCandidates(input).fold(0) { acc, candidate ->
            val text = input.substring(acc, candidate.startIndex)
            val span = spanGenerator(candidate)
            result.add(TextOrSpan.Text(text))
            result.add(TextOrSpan.Span(span))
            candidate.endIndex
        }
        val text = input.substring(lastIndex)
        result.add(TextOrSpan.Text(text))

        return result
    }

    private fun emojiToAlias(
        emoji: Emoji,
        fitzpatrickList: List<Fitzpatrick>,
        fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
    ): String = when (fitzpatrickAction) {
        FitzpatrickAction.PARSE -> {
            val fitzpatrickLabel = if (fitzpatrickList.isEmpty()) {
                ""
            } else {
                fitzpatrickList.joinToString("|", "|") {
                    it.name.lowercase()
                }
            }
            ":${emoji.aliases[0]}$fitzpatrickLabel:"
        }
        FitzpatrickAction.REMOVE -> ":${emoji.aliases[0]}:"
        FitzpatrickAction.IGNORE -> {
            val unicodes = fitzpatrickList.joinToString("") { it.unicode }
            ":${emoji.aliases[0]}:${unicodes}"
        }
    }

    private fun getUnicodeCandidates(text: String): Sequence<UnicodeCandidate> {
        return generateSequence (
            seedFunction = { getNextUnicodeCandidate(text, 0) },
            nextFunction = { prev ->
                getNextUnicodeCandidate(text, prev.endIndex)
            }
        )
    }

    private fun parseFromUnicode(
        input: String,
        transformer: (UnicodeCandidate) -> String
    ): String = parseToSpanList(input, transformer).joinToString ("") {
        when (it) {
            is TextOrSpan.Text -> it.text
            is TextOrSpan.Span -> it.box
        }
    }

    private fun getEmojiEndPos(text: String, startPos: Int): Int {
        val best = (startPos + 1..text.length).fold(-1) { acc, i ->
            val status = EmojiTrieStore.isEmoji(text.substring(startPos, i))
            when (status) {
                EmojiTrie.Matches.EXACTLY,
                EmojiTrie.Matches.POSSIBLY -> i
                EmojiTrie.Matches.IMPOSSIBLE -> return acc
            }
        }
        return best
    }
}
