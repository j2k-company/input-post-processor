import kotlinx.serialization.Serializable
import platform.posix.exit

@Serializable
enum class Action {
    SHUTDOWN {
        override fun run() {
            exit(0)
        }
    };
    abstract fun run()
}
