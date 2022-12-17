package com.github.seisuke.kemoji

class EmojiParser {

    data class UnicodeCandidate(
        val emoji: Emoji,
        val startIndex: Int,
        val endIndex: Int,
        val fitzpatrickList: List<Fitzpatrick> = emptyList(),
    )

    companion object {

        fun parseToAliases(
            input: String,
            fitzpatrickAction: FitzpatrickAction = FitzpatrickAction.PARSE
        ): String {
            val emojiTransformer: (UnicodeCandidate) -> String = { uc ->
                emojiToAlias(uc.emoji, uc.fitzpatrickList, fitzpatrickAction)
            }
            return parseFromUnicode(input, emojiTransformer)
        }

        fun emojiToAlias(
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

        private fun parseFromUnicode(
            input: String,
            transformer: (UnicodeCandidate) -> String
        ): String {
            val sb = StringBuilder(input.length)
            val lastIndex = getUnicodeCandidates(input).fold(0) { acc, candidate ->
                sb.append(input, acc, candidate.startIndex)
                sb.append(transformer(candidate))
                candidate.endIndex
            }
            return sb.append(input.substring(lastIndex)).toString()
        }

        private fun getUnicodeCandidates(text: String): Sequence<UnicodeCandidate> {
            return generateSequence (
                seedFunction = { getNextUnicodeCandidate(text, 0) },
                nextFunction = { prev ->
                    getNextUnicodeCandidate(text, prev.endIndex)
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

            val fitzpatrickList = Fitzpatrick.fitzpatrickRegex.findAll(
                text.substring(index, endPos)
            ).mapNotNull {result ->
                Fitzpatrick.fitzpatrickFromUnicode(result.value)
            }.toList()

            return UnicodeCandidate(
                emoji,
                index,
                endPos,
                fitzpatrickList,
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
