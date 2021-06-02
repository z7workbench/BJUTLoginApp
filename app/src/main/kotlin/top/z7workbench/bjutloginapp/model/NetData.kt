package top.z7workbench.bjutloginapp.model

import android.os.Parcel
import android.os.Parcelable

data class NetData(
    val time: Int = -1,
    val flow: Long = -1L,
    val fee: Float = -1F,
    val remained: String = "",
    val exceeded: String = "",
    val percent: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readLong(),
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt()
    )

    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(time)
        dest.writeLong(flow)
        dest.writeFloat(fee)
        dest.writeString(remained)
        dest.writeString(exceeded)
        dest.writeInt(percent)
    }

    companion object CREATOR : Parcelable.Creator<NetData> {
        override fun createFromParcel(parcel: Parcel): NetData {
            return NetData(parcel)
        }

        override fun newArray(size: Int): Array<NetData?> {
            return arrayOfNulls(size)
        }
    }
}
