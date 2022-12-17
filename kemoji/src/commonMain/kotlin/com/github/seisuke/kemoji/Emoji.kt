package com.github.seisuke.kemoji

data class Emoji(
    val emoji: String,
    val description: String,
    val category: String,
    val aliases: List<String>,
    val tags: List<String>,
    val unicodeVersion: UnicodeVersion,
    val iosVersion: Float,
    val fitzpatrickIndex: List<Int>,
    val vs16Index: List<Int>,
)

sealed class UnicodeVersion {
    object Empty : UnicodeVersion()
    class Version(val version: Float) : UnicodeVersion()
}
