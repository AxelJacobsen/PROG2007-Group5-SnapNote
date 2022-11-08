package com.example.myapplication

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class NoteActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        imageView = findViewById(R.id.imageView)

        val image = intent.getParcelableExtra<ImageClass>("picture")

        if (image != null) {
            if (image.bitmap != null){
                imageView.setImageBitmap(image.bitmap)
            } else if (image.uri != null){
                imageView.setImageURI(image.uri)
            }
        }
    }
}