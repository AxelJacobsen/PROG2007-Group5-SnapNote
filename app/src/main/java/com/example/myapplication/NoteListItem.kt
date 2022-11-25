package com.example.myapplication

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class NoteListItem(
    var menuItemName: String,
    var isFirstItem: Boolean,
    var menuItemThumbnail: Bitmap?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readBoolean(),
        parcel.readParcelable(Bitmap::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(menuItemName)
        parcel.writeBoolean(isFirstItem)
        parcel.writeParcelable(menuItemThumbnail, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NoteListItem> {
        override fun createFromParcel(parcel: Parcel): NoteListItem {
            return NoteListItem(parcel)
        }

        override fun newArray(size: Int): Array<NoteListItem?> {
            return arrayOfNulls(size)
        }
    }
}