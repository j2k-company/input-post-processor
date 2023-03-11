package exception


class VkCodeNotFoundException(val char: Char, message: String)
    : RuntimeException(message) {

    /**
    * The message describing the error and including a placeholder($char) for the character.
    */
    override val message: String?
        get() = super.message?.replace("\$char", char.toString())
}
