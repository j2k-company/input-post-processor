import kotlinx.serialization.Serializable
import platform.posix.exit
import platform.windows.SW_HIDE
import platform.windows.SW_SHOW

@Serializable
enum class Action {
    SHUTDOWN {
        override fun run() {
            exit(0)
        }
    },
    SHOW_WINDOW {
        override fun run() {
            ShowWindow(SW_SHOW)
        }
    },
    HIDE_WINDOW {
        override fun run() {
            ShowWindow(SW_HIDE)
        }
    };
    abstract fun run()
}
