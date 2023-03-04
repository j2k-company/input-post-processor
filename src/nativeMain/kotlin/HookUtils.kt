import kotlinx.cinterop.invoke
import platform.windows.*

fun setHook(type: Int, callback: HOOKPROC): HHOOK? =
    SetWindowsHookEx!!(type, callback, null, 0u)

fun removeHook(hook: HHOOK): Boolean = UnhookWindowsHookEx(hook).toBoolean()

fun nextHook(hook: HHOOK, nCode: Int, wParam: WPARAM, lParam: LPARAM) =
    CallNextHookEx(hook, nCode, wParam, lParam)

