import exception.VkCodeNotFoundException
import kotlinx.cinterop.*
import platform.windows.*

class InputSynthesizer {

    // TODO: add keyboard layout handling
    fun sendText(text: String) {
        val vkData: List<VkData> = getKeyPressDataFromText(text)

        releaseModifierKeys()

        vkData.forEach {
            sendKeyPress(vkCode = it.vkCode, shiftPressed = it.shift)
        }
    }

    private fun getKeyPressDataFromText(text: String): List<VkData> {
        val data = mutableListOf<VkData>()

        for (char in text) {
            val vkData = getVkData(char) ?: continue
            data += vkData
        }

        return data
    }

    private fun getVkData(char: Char): VkData? {
        return try {
            vkKeyScan(char, getKeyboardLayout())
        } catch (e: VkCodeNotFoundException) {
            getKeyboardLayoutList().forEach {
                try {
                    return vkKeyScan(char, it)
                } catch (_: VkCodeNotFoundException) { }
            }

            null
        }
    }

    fun releaseModifierKeys() {
        sendKeyPress(VK_LCONTROL)
        sendKeyPress(VK_RCONTROL)
        sendKeyPress(VK_LSHIFT)
        sendKeyPress(VK_RSHIFT)
    }

    /**
     * Sends WM_KEYDOWN and WM_KEYUP events with the given virtual-ley code
     *
     * NOTE: for the method to work correctly, call the [releaseModifierKeys] method before using it
     *
     * @param vkCode virtual-key code of the key to press
     * @param shiftPressed a flag indicating whether the Shift key must be pressed.
     *
     * If set to **true**, this method will also send Shift key press and release events.
     *
     * @see releaseModifierKeys
     */
    fun sendKeyPress(vkCode: Int, shiftPressed: Boolean = false) {
        val cbSize = sizeOf<INPUT>().toInt()

        memScoped {
            if (shiftPressed) {
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
                    cbSize
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
                    cbSize
                )
            }
        }
    }

    private fun createInputs(data: List<InputData>): CArrayPointer<INPUT> = memScoped {
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

    private fun createKeyInputData(vkCode: Int, isDown: Boolean = true) = KeyboardInput(
        wVk = vkCode.toUShort(),
        wScan = 0u,
        dwFlags = (if (isDown) 0 else KEYEVENTF_KEYUP).toUInt(),
        time = 0u,
        dwExtraInfo = NULL.toLong().toULong()
    )
}