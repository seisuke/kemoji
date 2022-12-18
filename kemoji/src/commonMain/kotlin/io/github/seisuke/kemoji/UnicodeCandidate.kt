package io.github.seisuke.kemoji

data class UnicodeCandidate(
    val emoji: Emoji,
    val startIndex: Int,
    val endIndex: Int,
    val fitzpatrickList: List<Fitzpatrick> = emptyList(),
)
