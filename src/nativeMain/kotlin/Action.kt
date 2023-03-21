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
    },
    ADD_SUB {
        override fun run() {
            ShowWindow(SW_SHOW)
            print("Input key:")
            val key = readln()
            print("Input substitution:")
            val substitution = readln()

            preferences.substitutions[key] = substitution
            println("Substitutions successfully added. You can hide window")
            preferences.save(PREFERENCES_PATH)
        }
    };
    abstract fun run()
}
