import kotlinx.cinterop.pointed
import kotlinx.cinterop.staticCFunction
import kotlinx.cinterop.toCPointer
import platform.windows.*


private var hook: HHOOK? = null

private fun eventHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
    return if (!KeyboardHook.processHook(nCode, wParam, lParam))
        nextHook(hook!!, nCode, wParam, lParam)
    else 1
}

object KeyboardHook {
    fun init() {
        hook = setHook(WH_KEYBOARD_LL, staticCFunction(::eventHook))
    }

    fun processHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): Boolean {
        if (nCode < 0)
            return false

        val info = lParam.toCPointer<KBDLLHOOKSTRUCT>()!!.pointed
        println("getting vkCode - ${info.vkCode}")

        return false
    }

    fun dispose() {
        removeHook(hook!!)
    }
}
