package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

// class Displays a single recipe page, and recieves data from pracel
class NoteDisplay : AppCompatActivity() {
    //Define interactables
    lateinit var noteNameDisplay: TextView
    lateinit var noteImageDisplay: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note_display)
        //Recieve and unpack parcel
        val noteData: NoteListItem? = intent.getParcelableExtra("extraData")
        val actualData: NoteListItem = noteData!!
        //Bind intractables
        noteNameDisplay = findViewById(R.id.noteName)
        noteImageDisplay = findViewById(R.id.noteImage)
        //Set Data from parcel
        noteNameDisplay.text = actualData.menuItemName
        noteImageDisplay.setImageResource(actualData.menuItemThumbnail)
    }
}