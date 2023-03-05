import platform.windows.*

fun setHook(type: Int, callback: HOOKPROC): HHOOK {
    val hMod = GetModuleHandleA(null)
    return SetWindowsHookExA(type, callback, hMod, 0u)!!
}

fun removeHook(hook: HHOOK): Boolean = UnhookWindowsHookEx(hook).toBoolean()

fun nextHook(hook: HHOOK, nCode: Int, wParam: WPARAM, lParam: LPARAM) =
    CallNextHookEx(hook, nCode, wParam, lParam)

