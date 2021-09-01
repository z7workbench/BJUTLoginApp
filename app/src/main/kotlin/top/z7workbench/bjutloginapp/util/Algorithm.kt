package top.z7workbench.bjutloginapp.util

import android.os.Bundle
import androidx.core.os.bundleOf
import top.z7workbench.bjutloginapp.model.BundledSyncData
import top.z7workbench.bjutloginapp.model.NetData
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

fun processSyncData(string: String, pack: Int): BundledSyncData {
    val bundle = bundleOf()
    val regex = """time='(.*?)';flow='(.*?)';fsele=1;fee='(.*?)'""".toRegex()
    val result = regex.find(string)
    if (result != null && result.groups.isNotEmpty()) {
        bundle.putBoolean("status", true)
        val time = result.groups[1]?.value?.toInt() ?: -1
        val flow = (result.groups[2]?.value?.toLong() ?: -1L) * 1024
        val fee = (result.groups[3]?.value?.toFloat() ?: -1F) / 10000

        // exceededByteSizeBundle
        val sizeOfMoneyInByte = (fee / 100 / 0.2 * 1024).toLong()
        val stringOfMoneyByte = formatByteSize(sizeOfMoneyInByte * 1024)
        val sizeOfUsedExtraInByte =
            if (flow > pack * 1024 * 1024)
                flow - pack * 1024 * 1024
            else
                0L
        val stringOfUsedExtraByte = formatByteSize(sizeOfUsedExtraInByte * 1024)
        val percent = if (sizeOfMoneyInByte + sizeOfUsedExtraInByte != 0L)
            sizeOfUsedExtraInByte * 100L / (sizeOfMoneyInByte + sizeOfUsedExtraInByte) else 0L
        val data = NetData(
            time, flow, fee, stringOfMoneyByte, stringOfUsedExtraByte, percent.toInt()
        )
        return BundledSyncData(true, data)
    } else {
        return BundledSyncData()
    }
}

fun percentOfPackage(flow: Long, pack: Int): Double =
    if (flow >= 0) (flow.toDouble() / 1024 / 1024 / 1024) / pack * 100 else 0.0