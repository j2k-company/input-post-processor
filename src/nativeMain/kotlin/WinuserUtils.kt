import exception.VkCodeNotFoundException
import kotlinx.cinterop.*
import platform.windows.*

fun setHook(type: Int, callback: HOOKPROC): HHOOK {
    val hMod = GetModuleHandleA(null)
    return SetWindowsHookExA(type, callback, hMod, 0u)!!
}

fun removeHook(hook: HHOOK): Boolean = UnhookWindowsHookEx(hook).toBoolean()

fun nextHook(hook: HHOOK, nCode: Int, wParam: WPARAM, lParam: LPARAM) =
    CallNextHookEx(hook, nCode, wParam, lParam)

fun keyPressed(keyCode: Int) =
    (GetKeyState(keyCode).toInt() and 0x8000) == 0x8000

fun vkKeyScan(char: Char, hkl: HKL): VkData {
    val vkScan = VkKeyScanExA(char.code.toByte(), hkl).toInt()
    if (vkScan and 0x8080 == 0x8080)
        throw VkCodeNotFoundException(char,
            "Could not get virtual code of the '\$char' for this keyboard layout")

    val modifierData = vkScan shr 8

    return VkData(
        vkCode = vkScan and 0xff,
        shift = modifierData and 1 == 1,
        control = modifierData and 2 == 2,
        alt = modifierData and 4 == 4,
        hkl = hkl
    )
}

fun getKeyboardLayout() = GetKeyboardLayout(
    GetWindowThreadProcessId(GetForegroundWindow(), null)
)!!


fun getKeyboardLayoutList(): List<HKL> {
    val count = GetKeyboardLayoutList(0, null)
    if (count == 0) {
        throwLastError()
    }

    memScoped {
        val layouts = allocArray<HKLVar>(count)

        if (GetKeyboardLayoutList(count, layouts) == 0) {
            throwLastError()
        }

        return cArrayToList(count, layouts).map { it.value!! }
    }

}

// TODO: write the functionality for error handling
fun getLastError(): Pair<Int, String> {
    TODO("Not yet implemented")
}

fun throwLastError(): Nothing {
    val (errorCode, message) = getLastError()
    TODO("Not yet implemented")
//    throw Exception()
}

fun activateKeyboardLayout(hkl: HKL) =
    ActivateKeyboardLayout(hkl, KLF_SETFORPROCESS) ?: throwLastError()

//fun sendLangChangeRequest(hkl: HKL) {
//    PostMessage!!(GetForegroundWindow(), WM_INPUTLANGCHANGEREQUEST.toUInt(), 0u, hkl.toLong())
//}
//
//fun previousKeyboardLayout() = memScoped {
//    val zero = alloc<IntVar>().apply { value = 0 }
//    val hkl = zero.reinterpret<HKL__>().ptr
//
//    activateKeyboardLayout(hkl)
//}
