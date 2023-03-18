import kotlinx.cinterop.*
import platform.windows.*


private var hook: HHOOK? = null

private fun eventHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
    return if (!InputProcessor.processHook(nCode, wParam, lParam))
        nextHook(hook!!, nCode, wParam, lParam)
    else 1
}

fun initHook() {
    hook = setHook(WH_KEYBOARD_LL, staticCFunction(::eventHook))
}

fun disposeHook() {
    if (hook != null) {
        removeHook(hook!!)
        hook = null
    }
}

fun main() {
    initHook()

    memScoped {
        val msg = alloc<MSG>()
        while (GetMessage!!(msg.ptr, null, 0u, 0u).toBoolean()) {
            TranslateMessage(msg.ptr)
            DispatchMessage!!(msg.ptr)
        }
    }
    disposeHook()
}
