import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import platform.posix.*


typealias HotKey = @Serializable(with = HotKeySerializer::class) VkData

@Serializable
data class Preferences(
    val substitutions: MutableMap<String, String>,
    @SerialName("hot_keys")
    val hotKeys: MutableMap<HotKey, Action>
) {
    fun save(path: String) {
        writeAllText(path, Json.encodeToString(this))
    }
}

fun loadPreferences(filePath: String): Preferences {
    val jsonString = readAllText(filePath)
    return Json.decodeFromString(jsonString)
}

// from https://www.nequalsonelifestyle.com/2020/11/16/kotlin-native-file-io/
private fun readAllText(filePath: String): String {
    val returnBuffer = StringBuilder()
    val file = fopen(filePath, "r") ?: throw IllegalArgumentException("Cannot open input file $filePath")

    try {
        memScoped {
            val readBufferLength = 64 * 1024
            val buffer = allocArray<ByteVar>(readBufferLength)
            var line = fgets(buffer, readBufferLength, file)?.toKString()
            while (line != null) {
                returnBuffer.append(line)
                line = fgets(buffer, readBufferLength, file)?.toKString()
            }
        }
    } finally {
        fclose(file)
    }

    return returnBuffer.toString()
}

// from https://www.nequalsonelifestyle.com/2020/11/16/kotlin-native-file-io/
fun writeAllText(filePath:String, text:String) {
    val file = fopen(filePath, "w") ?:
    throw IllegalArgumentException("Cannot open output file $filePath")
    try {
        memScoped {
            if(fputs(text, file) == EOF) throw Error("File write error")
        }
    } finally {
        fclose(file)
    }
}
