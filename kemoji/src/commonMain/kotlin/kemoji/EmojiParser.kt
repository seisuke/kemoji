package kemoji

class EmojiParser {

    private data class AliasCandidate(
        val unicode: String,
        val fitzpatrick: Fitzpatrick?,
        val range: IntRange,
    )

    data class UnicodeCandidate(
        val emoji: Emoji,
        val fitzpatrick: Fitzpatrick?,
        val startIndex: Int,
    ) {
        val endIndex = startIndex + emoji.emoji.length
        val fitzpatrickEndIndex = endIndex + if (fitzpatrick != null) {
            2
        } else {
            0
        }
    }

    companion object {

        fun parseToAliases(
            input: String,
            fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
        ): String {
            val emojiTransformer: (UnicodeCandidate) -> String = { uc ->
                when (fitzpatrickAction) {
                    FitzpatrickAction.PARSE -> if (uc.fitzpatrick != null) {
                        ":${uc.emoji.aliases[0]}|${uc.fitzpatrick.name.lowercase()}:"
                    } else {
                        ":${uc.emoji.aliases[0]}:"
                    }
                    FitzpatrickAction.REMOVE -> ":${uc.emoji.aliases[0]}:"
                    FitzpatrickAction.IGNORE ->
                        ":${uc.emoji.aliases[0]}:${uc.fitzpatrick?.unicode ?: ""}"
                }
            }
            return parseFromUnicode(input, emojiTransformer)
        }

        private fun parseFromUnicode(
            input: String,
            transformer: (UnicodeCandidate) -> String
        ): String {
            var prev = 0
            val sb = StringBuilder(input.length)
            val replacements: List<UnicodeCandidate> = getUnicodeCandidates(input)
            for (candidate in replacements) {
                sb.append(input, prev, candidate.startIndex)
                sb.append(transformer(candidate))
                prev = candidate.fitzpatrickEndIndex
            }
            return sb.append(input.substring(prev)).toString()
        }

        private fun getUnicodeCandidates(input: String): List<UnicodeCandidate> {
            val candidateList = mutableListOf<UnicodeCandidate>()
            var i = 0
            var nextCan = getNextUnicodeCandidate(input.toCharArray(), i)
            while (nextCan != null) {
                candidateList.add(nextCan)
                i = nextCan.fitzpatrickEndIndex
                nextCan = getNextUnicodeCandidate(input.toCharArray(), i)
            }
            return candidateList
        }

        internal fun getNextUnicodeCandidate(chars: CharArray, start: Int): UnicodeCandidate? {
            for (i in start until chars.size) {
                val emojiEnd: Int = getEmojiEndPos(chars, i)
                if (emojiEnd != -1) {
                    val emoji = EmojiManager.getByUnicode(
                        chars.concatToString(i, i + (emojiEnd - i))
                    ) ?: return null
                    val fitzpatrick = if (emojiEnd + 2 <= chars.size) {
                        val unicode = chars.concatToString(emojiEnd, emojiEnd + 2)
                        Fitzpatrick.fitzpatrickFromUnicode(unicode)
                    } else {
                        null
                    }
                    return UnicodeCandidate(
                        emoji,
                        fitzpatrick,
                        i
                    )
                }
            }
            return null
        }

        private fun getEmojiEndPos(text: CharArray, startPos: Int): Int {
            var best = -1
            for (j in startPos + 1..text.size) {
                val status = EmojiManager.isEmoji(text.concatToString(), startPos, j)
                when (status) {
                    EmojiTrie.Matches.EXACTLY -> {
                        best = j
                    }
                    EmojiTrie.Matches.IMPOSSIBLE -> return best
                    else -> Unit
                }
            }
            return best
        }
    }
}
