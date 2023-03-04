import kotlinx.cinterop.staticCFunction
import platform.windows.*

class KeyboardHook {
    private val hook: HHOOK
    private var processHook = staticCFunction(::_processHook)

    init {
        hook = setHook(WH_KEYBOARD, processHook)!!
    }

    private fun _processHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): LRESULT {
        // process
        return nextHook(hook, nCode, wParam, lParam)
    }

    fun disable() = removeHook(hook)
}