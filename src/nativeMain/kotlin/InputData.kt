import platform.windows.HKL

data class VkData(
    val vkCode: Int,
    val hkl: HKL,
    val shift: Boolean = false,
    val alt: Boolean = false,
    val control: Boolean = false,
    val win: Boolean = false
)

sealed class InputData

data class HardwareInput(
    val uMsg: UInt,
    val wParamL: UShort,
    val wParamH: UShort
) : InputData()

data class KeyboardInput(
    val wVk: UShort,
    val wScan: UShort,
    val dwFlags: UInt,
    val time: UInt,
    val dwExtraInfo: ULong
) : InputData()

data class MouseInput(
    val dX: Int,
    val dY: Int,
    val mouseData:  UInt,
    val dwFlags: UInt,
    val time: UInt,
    val dwExtraInfo: ULong
) : InputData()
