import kotlinx.cinterop.*
import platform.windows.*


fun main() {
    KeyboardHook.init()

    memScoped {
        val msg = alloc<MSG>()
        while (GetMessage!!(msg.ptr, null, 0u, 0u).toBoolean()) {
            TranslateMessage(msg.ptr)
            DispatchMessage!!(msg.ptr)
        }
    }
    KeyboardHook.dispose()
}
