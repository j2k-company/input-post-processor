import platform.windows.GetKeyState
import platform.windows.WINBOOL

fun WINBOOL.toBoolean() = this == 1

fun keyPressed(keyCode: Int) =
    (GetKeyState(keyCode).toInt() and 0x8000) == 0x8000
