import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class HotKeySerializer : KSerializer<VkData> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("VkData", PrimitiveKind.STRING)

    // TODO: Rewrite deserializer
    override fun deserialize(decoder: Decoder): VkData {
        val string = decoder.decodeString().uppercase()
        val keys = string.split("+")

        val mainKey = keys.find {
            it != "SHIFT" && it != "ALT" && it != "CTRL" && it != "WIN"
        }!!.first()

        return VkData(
            vkCode = if (mainKey.isLetterOrDigit()) {
                mainKey.code
            } else {
                vkKeyScan(mainKey, getKeyboardLayout()).vkCode
            },
            shift = "SHIFT" in keys,
            alt = "ALT" in keys,
            control = "CTRL" in keys,
            win = "WIN" in keys
        )
    }

    override fun serialize(encoder: Encoder, value: VkData) {
        val buf = StringBuilder()
        value.run {
            if (shift) buf.append("SHIFT+")
            if (alt) buf.append("ALT+")
            if (control) buf.append("CTRL+")
            if (win) buf.append("WIN+")

            val char = if (vkCode.toChar().isLetterOrDigit()) {
                vkCode.toChar()
            } else {
                keyCodeToChar(vkCode, shiftPressed = false, getKeyboardLayout())
            }

            buf.append(char)
        }

        encoder.encodeString(buf.toString())
    }
}