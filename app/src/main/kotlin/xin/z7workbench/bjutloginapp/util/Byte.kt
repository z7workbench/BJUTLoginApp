package xin.z7workbench.bjutloginapp.util

import kotlin.math.absoluteValue

enum class ByteSize(val display: String) {
    B("B"),
    KB("kB"),
    MB("MB"),
    GB("GB"),
    TB("TB"),
    PB("PB")
}

fun formatByteSize(byte: Long): String {
    var absByte = byte.absoluteValue.toDouble()
    var byteSize = ByteSize.B
    val nine = 921L
    if (absByte >= nine) {
        byteSize = ByteSize.KB
        absByte /= 1024
    }
    if (absByte >= nine) {
        byteSize = ByteSize.MB
        absByte /= 1024
    }
    if (absByte >= nine) {
        byteSize = ByteSize.GB
        absByte /= 1024
    }
    if (absByte >= nine) {
        byteSize = ByteSize.TB
        absByte /= 1024
    }
    if (absByte >= nine) {
        byteSize = ByteSize.PB
        absByte /= 1024
    }
    if (byte < 0) absByte = -absByte

    return "$absByte ${byteSize.display}"
}