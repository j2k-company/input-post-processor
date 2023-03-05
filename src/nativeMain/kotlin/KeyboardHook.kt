import kotlinx.cinterop.staticCFunction
import platform.windows.*


private val hook: HHOOK = setHook(WH_KEYBOARD_LL, staticCFunction(::eventHook))

private fun eventHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
    return if (!KeyboardHook.processHook(nCode, wParam, lParam))
        nextHook(hook, nCode, wParam, lParam)
    else 1
}

object KeyboardHook {
    fun processHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): Boolean {
        if (nCode < 0)
            return false

        return false
    }

    fun dispose() {
        removeHook(hook)
    }
}
