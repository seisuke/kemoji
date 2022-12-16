package kemoji

/**
 * Enum used to indicate what should be done when a Fitzpatrick modifier is
 * found.
 */
enum class FitzpatrickAction {
    /**
     * Tries to match the Fitzpatrick modifier with the previous emoji
     */
    PARSE,

    /**
     * Removes the Fitzpatrick modifier from the string
     */
    REMOVE,

    /**
     * Ignores the Fitzpatrick modifier (it will stay in the string)
     */
    IGNORE
}
