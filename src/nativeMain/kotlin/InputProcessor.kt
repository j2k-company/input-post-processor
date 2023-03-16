import kotlinx.cinterop.pointed
import kotlinx.cinterop.toCPointer
import platform.windows.*

object InputProcessor {
    private val inputSynthesizer = InputSynthesizer()
    private val preferences = loadPreferences("substitutions.json")

    private val cache = mutableListOf<Char>()

    private var keyInput = false

    fun processHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): Boolean {
        if (nCode < 0)
            return false

        val info = lParam.toCPointer<KBDLLHOOKSTRUCT>()!!.pointed
        val keyCode = info.vkCode.toInt()

        when (wParam.toInt()) {
            WM_KEYDOWN, WM_SYSKEYDOWN -> {
                if (keyCode == 0x35 && keyPressed(VK_SHIFT)) {
                    keyInput = !keyInput

                    if (!keyInput) {
                        val replaced = replaceInput(cache.joinToString(""))
                        cache.clear()
                        return replaced
                    }

                    return false
                }

                if (keyInput && isPrintable(keyCode) && !modifierKeysPressed()) {
                    cache.add(keyCodeToChar(
                        keyCode,
                        info.scanCode.toInt(),
                        keyPressed(VK_SHIFT) xor keyToggled(VK_CAPITAL),
                        getKeyboardLayout()
                    )!!)
                } else if (keyInput && keyCode == VK_BACK) {
                    if (cache.size == 0) {
                        keyInput = !keyInput
                    } else {
                        cache.removeAt(cache.lastIndex)
                    }
                }
            }

            WM_KEYUP, WM_SYSKEYUP -> {}
        }

        return false
    }

    // NOTE: vkCode is virtual-key code of current pressed key
    private fun isHotKey(vkCode: Int) {
        TODO("Not yet implemented")
    }

    fun modifierKeysPressed() =
        keyPressed(VK_CONTROL) || keyPressed(VK_MENU) || keyPressed(VK_LWIN) || keyPressed(VK_RWIN)

    fun isPrintable(keyCode: Int) = when (keyCode) {
        in 0x30..0x39,
        in 0x41..0x5A,
        in VK_NUMPAD0..VK_DIVIDE,
        in VK_OEM_1..VK_OEM_3,
        in VK_OEM_4..VK_OEM_8,
        VK_OEM_102 -> true

        else -> false
    }

    private fun replaceInput(key: String): Boolean {
        if (key in preferences.substitutions.keys) {
            inputSynthesizer.apply {
                releaseModifierKeys()
                (0..key.length).forEach { _ ->
                    sendKeyPress(VK_BACK)
                }

                sendText(
                    preferences.substitutions[key]!!
                )
            }
            return true
        }

        return false
    }
}
