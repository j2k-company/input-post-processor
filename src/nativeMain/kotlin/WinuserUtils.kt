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

// NOTE:XXX:FIXME: so far it supports only letters and numbers
fun sendInputChar(symbol: Char) {
    memScoped {
        val vkCode = symbol.uppercaseChar().code

        if (symbol.isUpperCase()) {
            SendInput(
                4u,
                createInputs(
                    listOf(
                        createKeyInputData(VK_SHIFT),
                        createKeyInputData(vkCode),
                        createKeyInputData(vkCode, isDown = false),
                        createKeyInputData(VK_SHIFT, isDown = false)
                    )
                ),
                sizeOf<INPUT>().toInt()
            )
        } else {
            SendInput(
                2u,
                createInputs(
                    listOf(
                        createKeyInputData(vkCode),
                        createKeyInputData(vkCode, isDown = false),
                    )
                ),
                sizeOf<INPUT>().toInt()
            )
        }
    }
}

fun createInputs(data: List<InputData>): CArrayPointer<INPUT> = memScoped {
    allocArray(data.size) { i ->
        when (val inputData = data[i]) {
            is MouseInput -> {
                type = 0u
                inputData.run {
                    mi.dx = dX
                    mi.dy = dY
                    mi.mouseData = mouseData
                    mi.dwFlags = dwFlags
                    mi.time = time
                    mi.dwExtraInfo = dwExtraInfo
                }
            }

            is KeyboardInput -> {
                type = 1u
                inputData.run {
                    ki.wVk = wVk
                    ki.wScan = wScan
                    ki.dwFlags = dwFlags
                    ki.time = time
                    ki.dwExtraInfo = dwExtraInfo
                }
            }

            is HardwareInput -> {
                type = 2u
                inputData.run {
                    hi.uMsg = uMsg
                    hi.wParamL = wParamL
                    hi.wParamH = wParamH
                }
            }
        }
    }
}

fun createKeyInputData(vkCode: Int, isDown: Boolean = true) = KeyboardInput(
    wVk = vkCode.toUShort(),
    wScan = 0u,
    dwFlags = (if (isDown) 0 else KEYEVENTF_KEYUP).toUInt(),
    time = 0u,
    dwExtraInfo = NULL.toLong().toULong()
)
