import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class Emoji(
    val emoji: String,
    val description: String,
    val category: String,
    val aliases: List<String>,
    val tags: List<String>,
    @SerialName("unicode_version")
    val unicodeVersion: UnicodeVersion,
    @SerialName("ios_version")
    val iosVersion: Float,
    @SerialName("skin_tones")
    val supportsFitzpatrick: Boolean = false,
)

@Serializable(with = OperatingModeSafeSerializer::class)
sealed class UnicodeVersion {
    fun toPoetString(): String = when(this) {
        is Empty -> "UnicodeVersion.Empty"
        is Version -> "UnicodeVersion.Version(${version}f)"
    }

    object Empty : UnicodeVersion()
    class Version(val version: Float) : UnicodeVersion()
}

internal object OperatingModeSafeSerializer : KSerializer<UnicodeVersion> {
    override val descriptor = PrimitiveSerialDescriptor("OperatingMode", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: UnicodeVersion) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): UnicodeVersion {
        val string = decoder.decodeString()
        return if (string.isBlank()) {
            UnicodeVersion.Empty
        } else {
            UnicodeVersion.Version(string.toFloat())
        }
    }
}