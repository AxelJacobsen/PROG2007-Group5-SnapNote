package com.example.myapplication

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuView

data class NoteListItem(
    var menuItemName: String,
    var isFirstItem: Boolean,
    var menuItemThumbnail: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readBoolean(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(menuItemName)
        parcel.writeBoolean(isFirstItem)
        parcel.writeInt(menuItemThumbnail)
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