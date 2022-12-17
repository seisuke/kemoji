package kemoji

class EmojiParser {

    data class UnicodeCandidate(
        val emoji: Emoji,
        val fitzpatrick: Fitzpatrick?,
        val startIndex: Int,
        val endIndex: Int,
    ) {
        val fitzpatrickEndIndex = endIndex + if (fitzpatrick != null) { //TODO fix for multiple fitzpatrick
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
                emojiToAlias(uc.emoji, uc.fitzpatrick, fitzpatrickAction)
            }
            return parseFromUnicode(input, emojiTransformer)
        }

        fun emojiToAlias(
            emoji: Emoji,
            fitzpatrick: Fitzpatrick?,
            fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
        ): String = when (fitzpatrickAction) {
            FitzpatrickAction.PARSE -> if (fitzpatrick != null) {
                ":${emoji.aliases[0]}|${fitzpatrick.name.lowercase()}:"
            } else {
                ":${emoji.aliases[0]}:"
            }
            FitzpatrickAction.REMOVE -> ":${emoji.aliases[0]}:"
            FitzpatrickAction.IGNORE ->
                ":${emoji.aliases[0]}:${fitzpatrick?.unicode ?: ""}"
        }

        private fun parseFromUnicode(
            input: String,
            transformer: (UnicodeCandidate) -> String
        ): String {
            val sb = StringBuilder(input.length)
            val lastIndex = getUnicodeCandidates(input).fold(0) { acc, candidate ->
                sb.append(input, acc, candidate.startIndex)
                sb.append(transformer(candidate))
                candidate.fitzpatrickEndIndex
            }
            return sb.append(input.substring(lastIndex)).toString()
        }

        private fun getUnicodeCandidates(text: String): Sequence<UnicodeCandidate> {
            return generateSequence (
                seedFunction = { getNextUnicodeCandidate(text, 0) },
                nextFunction = { prev ->
                    getNextUnicodeCandidate(text, prev.fitzpatrickEndIndex)
                }
            )
        }

        private fun getNextUnicodeCandidate(text: String, startPos: Int): UnicodeCandidate? {
            val (index, endPos) = (startPos until text.length).asSequence().map { index ->
                index to getEmojiEndPos(text, index)
            }.firstOrNull { (_, endPos) ->
                endPos != -1
            } ?: return null

            val emoji = EmojiManager.getByUnicode(
                text.substring(index, endPos)
            ) ?: return null
            val fitzpatrick = if (endPos + 2 <= text.length) { //TODO fix for multiple fitzpatrick
                val unicode = text.substring(endPos, endPos + 2)
                Fitzpatrick.fitzpatrickFromUnicode(unicode)
            } else {
                null
            }
            return UnicodeCandidate(
                emoji,
                fitzpatrick,
                index,
                endPos,
            )
        }

        private fun getEmojiEndPos(text: String, startPos: Int): Int {
            val best = (startPos + 1..text.length).fold(-1) { acc, i ->
                val status = EmojiManager.isEmoji(text, startPos, i)
                when (status) {
                    EmojiTrie.Matches.EXACTLY,
                    EmojiTrie.Matches.POSSIBLY -> i
                    EmojiTrie.Matches.IMPOSSIBLE -> return acc
                }
            }
            return best
        }
    }
}
