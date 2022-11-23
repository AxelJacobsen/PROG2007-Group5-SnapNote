package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * class Displays a single recipe page, and recieves data from pracel
 */
class NoteDisplay : AppCompatActivity() {
    //Define interactables
    lateinit var noteImageDisplay: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_note)
        //Recieve and unpack parcel
        val noteData: NoteListItem? = intent.getParcelableExtra("extraData")
        val actualData: NoteListItem = noteData!!
        //Bind intractables
        noteImageDisplay = findViewById(R.id.noteBackground)
        //Set Data from parcel
        noteImageDisplay.setImageResource(actualData.menuItemThumbnail)


    }
}