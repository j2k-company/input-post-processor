import kotlinx.cinterop.*
import platform.windows.WINBOOL

fun WINBOOL.toBoolean() = this == 1

inline fun <reified T : CPointerVarOf<*>> cArrayToList(size: Int, cArray: CArrayPointer<T>) =
    (0 until size).map { cArray[it] }
