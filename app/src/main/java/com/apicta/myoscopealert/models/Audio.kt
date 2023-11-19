package com.apicta.myoscopealert.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

data class Audio(
    val uri: Uri?,
    val displayName: String?,
    val id: Long,
    val artist: String?,
    val data: String?,
    val duration: Int,
    val title: String?,
    val dateModified: Long,
) : Parcelable {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }
    // Implement Parcelable methods here

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        // Write each field to the parcel
        parcel.writeParcelable(uri, flags)
        parcel.writeString(displayName)
        parcel.writeLong(id)
        parcel.writeString(artist)
        parcel.writeString(data)
        parcel.writeInt(duration)
        parcel.writeString(title)
    }

    // Implement Parcelable.Creator interface
    companion object CREATOR : Parcelable.Creator<Audio> {
        override fun createFromParcel(parcel: Parcel): Audio {
            // Read each field from the parcel and return a new Audio object
            return Audio(
                parcel.readParcelable(Uri::class.java.classLoader),
                parcel.readString() ?: "",
                parcel.readLong(),
                parcel.readString() ?: "",
                parcel.readString() ?: "",
                parcel.readInt(),
                parcel.readString() ?: "",
                parcel.readLong(),
            )
        }

        override fun newArray(size: Int): Array<Audio?> {
            return arrayOfNulls(size)
        }
    }
}
