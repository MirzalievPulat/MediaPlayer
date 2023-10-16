package uz.frodo.mediaplayer.models

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

class Song(
    var id:Long, var artist: String?, var name: String?, var data: String?, var date:Long, var image: Bitmap?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(artist)
        parcel.writeString(name)
        parcel.writeString(data)
        parcel.writeLong(date)
        parcel.writeParcelable(image, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Song> {
        override fun createFromParcel(parcel: Parcel): Song {
            return Song(parcel)
        }

        override fun newArray(size: Int): Array<Song?> {
            return arrayOfNulls(size)
        }
    }
}