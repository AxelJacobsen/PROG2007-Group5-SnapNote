package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var TakePictureButton: Button
    private lateinit var AddImageButton: Button
    private val REQUEST_CODE_FOR_PIC = 1
    private val REQUEST_CODE_FOR_GALLERY = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TakePictureButton = findViewById(R.id.takeImageBtn)
        AddImageButton = findViewById(R.id.fromGalleryBtn)

        TakePictureButton.setOnClickListener {
            takeAPic()
        }
        AddImageButton.setOnClickListener {
            uploadFromGallery()
        }
    }

    private fun takeAPic(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),
                REQUEST_CODE_FOR_PIC)
        }else{
            val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePicIntent, REQUEST_CODE_FOR_PIC)
        }
    }

    private fun uploadFromGallery(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_FOR_GALLERY)
        }else{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_CODE_FOR_GALLERY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var image: ImageClass = ImageClass(null, null)
        if(requestCode==REQUEST_CODE_FOR_PIC && resultCode == Activity.RESULT_OK){
            image.bitmap = data?.extras?.get("data") as Bitmap
        }else if (requestCode == REQUEST_CODE_FOR_GALLERY && resultCode == Activity.RESULT_OK){
            image.uri = data?.data as Uri
        }
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("picture", image)
        startActivity(intent)
    }

}