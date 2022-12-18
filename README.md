# kemoji

🛟 Kotlin Multiplatform Framework Emoji Support Library 🛟

**kemoji** is a library to add Emoji support to your Kotlin Application.
This library contains character information about native emojis.

Inspired by [vdurmont/emoji-java](https://github.com/vdurmont/emoji-java).
The emoji data is based on the database files, unicode-emoji-test.txt and emoji.json from [github/gemoji](https://github.com/github/gemoji).
However, emoji.json doesn't contain some necessary emojis, so **kemoji** adds the below original emojis.

- handshake "🫱‍🫲"
- women holding hands "👩‍🤝‍👩"
- woman and man holding hands "👩‍🤝‍👨"
- men holding hands "👨‍🤝‍👨"
- kiss: person, person "🧑‍❤️‍💋‍🧑"
- couple with heart: person, person "🧑‍❤️‍🧑"

**kemoji** supports until Emoji 14.0 characters. Because gemoji has never supported Emoji 15.* yet.

[gemoji/issues/248](https://github.com/github/gemoji/issues/248)

### Gradle 

```gradle
//gradle kotlin DSL
implementation("io.github.seisuke:kemoji:0.1.0") //for common
implementation("io.github.seisuke:kemoji-jvm:0.1.0") //for JVM
implementation("io.github.seisuke:kemoji-js:0.1.0") //for Kotlin/JS
```
## How to use it?

### EmojiManager

The `EmojiManager` provides several static methods to search through the emoji database:

- `getForTag` returns all the emojis for a given tag
- `getForAlias` returns the emoji for an alias
- `isEmoji` checks if a string is an emoji
- `getAllTags` returns the available tags
- `getAll` returns all the emojis

### Emoji model

An `Emoji` is a Data class, which provides the following properties:

```kotlin
data class Emoji(
    val emoji: String,                  // returns unicode
    val description: String,
    val category: String,
    val aliases: List<String>,          // returns a list of aliases for this emoji
    val tags: List<String>,             // returns a list of tags for this emoji
    val unicodeVersion: UnicodeVersion, // returns unicode version enum. this emoji is supported since this versin.
    val iosVersion: Float,              // returns ios vesion value. this emoji is supported since this versin.
    val fitzpatrickIndex: List<Int>,    // returns a list of position of fitzpatrick codepoints.
    val vs16Index: List<Int>,           // returns a list of position of vs16 codepoints.
)
```

### Emoji List

An `EmojiList` holds all `Emoji`. It is generated from `EmojiGenerator#generateEmojiLists()`, and it based on database files, unicode-emoji-test.txt and emoji.json.qjF

### EmojiParser

To replace all the emoji's unicodes found in a string by their aliases, use `EmojiParser#parseToAliases(String)`.

For example:

```kotlin
val text = "An 😀awesome 😃string with a few 😉emojis!"
EmojiParser.parseToAliases(text) // => "An :grinning:awesome :smiley:string with a few :wink:emojis!"
```

## unicode-emoji-test.txt

`EmojiTest#emojisGetByUnicodeTest` and `EmojiTest#emojisAlias` pass tests of 4441 pattern emoji in [unicode-emoji-test.txt](./emojiListGenerator/src/main/resources/unicode-emoji-test.txt) expects unqualified them. 

## Available Emojis

See a json file [HERE](./emojiListGenerator/src/main/resources/emoji.json).
