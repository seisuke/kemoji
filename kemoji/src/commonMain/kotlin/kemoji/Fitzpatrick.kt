package kemoji

/**
 * Enum that represents the Fitzpatrick modifiers supported by the emojis.
 */
enum class Fitzpatrick(val unicode: String) {
    /**
     * Fitzpatrick modifier of type 1/2 (pale white/white)
     */
    TYPE_1_2("\uD83C\uDFFB"),

    /**
     * Fitzpatrick modifier of type 3 (cream white)
     */
    TYPE_3("\uD83C\uDFFC"),

    /**
     * Fitzpatrick modifier of type 4 (moderate brown)
     */
    TYPE_4("\uD83C\uDFFD"),

    /**
     * Fitzpatrick modifier of type 5 (dark brown)
     */
    TYPE_5("\uD83C\uDFFE"),

    /**
     * Fitzpatrick modifier of type 6 (black)
     */
    TYPE_6("\uD83C\uDFFF");

    companion object {
        fun fitzpatrickFromUnicode(unicode: String): Fitzpatrick? {
            return values().find { it.unicode == unicode }
        }
    }

}
