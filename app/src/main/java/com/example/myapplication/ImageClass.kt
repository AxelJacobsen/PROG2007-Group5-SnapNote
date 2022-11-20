package com.example.myapplication

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable

/**
 * Stores imagedata as a parcel
 */
data class ImageClass(var bitmap: Bitmap?, var uri: Uri?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Bitmap::class.java.classLoader),
        parcel.readParcelable(Uri::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(bitmap, flags)
        parcel.writeParcelable(uri, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageClass> {
        override fun createFromParcel(parcel: Parcel): ImageClass {
            return ImageClass(parcel)
        }

        override fun newArray(size: Int): Array<ImageClass?> {
            return arrayOfNulls(size)
        }
    }
}