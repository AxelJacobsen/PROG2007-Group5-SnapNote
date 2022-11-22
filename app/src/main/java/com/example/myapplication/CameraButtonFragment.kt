package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment

/**
 * Opens a dialog asking the user if they wish to take a picture or load from gallery
 */
class CameraButtonFragment : DialogFragment() {
    val CAMERA_PERM = 1
    val CAMERA_STRING = android.Manifest.permission.CAMERA
    val GALLERY_PERM = 2
    val GALLERY_STRING = android.Manifest.permission.READ_EXTERNAL_STORAGE
    var currentPermString = ""
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        return activity?.let{
            val builder = AlertDialog.Builder(it)
            builder.setMessage("New Note")
                .setNeutralButton("Take Picture") { _, _ ->
                    takeAPic()
                }
                .setPositiveButton("Upload Image") { _, _ ->
                    uploadFromGallery()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    /**
     * Requests permission for camera use and opens the camera
     */
    private fun takeAPic(){
        if (context?.checkSelfPermission(CAMERA_STRING) == PackageManager.PERMISSION_GRANTED){
            //opens camera
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(cameraIntent)
        } else {
            //Updates public permstring since you cant retrieve parameter in function variable
            currentPermString = CAMERA_STRING
            requestPermission.launch(CAMERA_STRING)
        }
    }

    /**
     * Requests permission for gallery use and opens the gallery
     */
    private fun uploadFromGallery(){
        if (context?.checkSelfPermission(GALLERY_STRING) == PackageManager.PERMISSION_GRANTED){
            //Opens gallery, due to lack of images saving this is untested
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getResult.launch(galleryIntent)
        } else {
            currentPermString = GALLERY_STRING
            requestPermission.launch(GALLERY_STRING)
        }
    }

    /**
     * Handles launching intent
     */
    private val getResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            /**
             * Greeting Mr.Teacher or whoever might be reading this. My name is Axel Jacobsen
             * and everything past this is never called. Why you may ask? WELL, let me tell you why.
             * Android studio has a built in function called onActivityResult, this function
             * is deprecated in the newer version of android studio. Therefore, i wished not to use
             * it. After days of research this was the only alternative, however, it didn't work.
             *
             * Now as to why this didn't work remained a mystery for a while, until desperation, and
             * the debugger tool told me that a default onActivityResult was catching the result
             * from the camera BEFORE this did. Why a deprecated function had priority is a mystery.
             * Internet could not help me since this is a relatively
             * new change. So here we are. If for whatever reason this code finally gets its turn
             * then it should work. However i have no way of testing it since android studio is
             * staunchly denying my every attempt to use new and updated code.
             */
            var outImage = ImageClass(null, null)
            if(result.resultCode == Activity.RESULT_OK){
                if ( result.resultCode==CAMERA_PERM){
                    outImage.bitmap = result.data?.extras?.get("data") as Bitmap
                }
                else if (result.resultCode == GALLERY_PERM){
                    outImage.uri = result.data?.data as Uri
                }
                val intent = Intent(requireContext(), NoteActivity::class.java)
                intent.putExtra("picture", outImage)
                startActivity(intent)
            }
        }

    /**
     * Requests permission to use camera and gallery
     */
    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            gotPermission: Boolean ->
            if (gotPermission){
                if (currentPermString ==CAMERA_STRING) {
                    val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    Toast.makeText(requireContext(), "Entered camera", Toast.LENGTH_SHORT).show()
                    getResult.launch(takePicIntent)
                } else if (currentPermString == GALLERY_STRING){
                    val galleryIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    getResult.launch(galleryIntent)
                } else {
                    Toast.makeText(requireContext(), "Illegal Access type", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Cant access camera or gallery data, update permissions in settings to access", Toast.LENGTH_SHORT).show()
            }
        }
}