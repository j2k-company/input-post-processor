import kotlinx.cinterop.pointed
import kotlinx.cinterop.toCPointer
import platform.windows.*

object InputProcessor {
    private val inputSynthesizer = InputSynthesizer()
    private var keyInput = false

    private val cache = mutableListOf<Char>()

    fun processHook(nCode: Int, wParam: WPARAM, lParam: LPARAM): Boolean {
        if (nCode < 0)
            return false

        val info = lParam.toCPointer<KBDLLHOOKSTRUCT>()!!.pointed
        val keyCode = info.vkCode.toInt()

        when (wParam.toInt()) {
            WM_KEYDOWN, WM_SYSKEYDOWN -> {
                val vkData = VkData(
                    vkCode = keyCode,
                    hkl = getKeyboardLayout(),
                    shift = keyPressed(VK_SHIFT) xor keyToggled(VK_CAPITAL),
                    alt = keyPressed(VK_MENU),
                    control = keyPressed(VK_CONTROL),
                    win = keyPressed(VK_LWIN) or keyPressed(VK_RWIN)
                )

                val hotKey = preferences.hotKeys.keys.firstOrNull(vkData::equalsWithoutHkl)

                if(hotKey != null) {
                    preferences.hotKeys[hotKey]?.run() ?: return true
                    return false
                }

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
                        vkData.vkCode, info.scanCode.toInt(),
                        vkData.shift, vkData.hkl!!
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
        val text = preferences.substitutions[key] ?: return false

        inputSynthesizer.apply {
            releaseModifierKeys()
            (0..key.length).forEach { _ -> sendKeyPress(VK_BACK) }

            sendText(text)
        }

        return true
    }
}
