package xin.z7workbench.bjutloginapp.util

import android.os.Bundle
import org.jetbrains.anko.bundleOf
import kotlin.math.absoluteValue
import kotlin.math.round

enum class ByteSize(val display: String) {
    B("B"),
    KB("kB"),
    MB("MB"),
    GB("GB"),
    TB("TB"),
    PB("PB");
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

    return "${round(absByte * 1000) / 1000} ${byteSize.display}"
}

fun exceededByteSizeBundle(flow: Long, pack: Int, money: Float): Bundle {
    val sizeOfMoneyInByte = (money / 100 / 0.2 * 1024).toLong()
    val stringOfMoneyByte = formatByteSize(sizeOfMoneyInByte * 1024)
    val sizeOfUsedExtraInByte =
            if (flow > pack * 1024 * 1024)
                flow - pack * 1024 * 1024
            else
                0L
    val stringOfUsedExtraByte = formatByteSize(sizeOfUsedExtraInByte * 1024)
    val percent = sizeOfUsedExtraInByte * 100L / (sizeOfMoneyInByte + sizeOfUsedExtraInByte)
    return bundleOf(
            "remained" to stringOfMoneyByte,
            "exceeded" to stringOfUsedExtraByte,
            "percent" to percent.toInt()
    )
}