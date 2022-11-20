package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

/**
 * Opens a dialog asking the user if they wish to take a picture or load from gallery
 */
class CameraButtonFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?) : Dialog {
        print("test")
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

    //requests permission, then opens camera
    private fun takeAPic(){
        val cameraPermits = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)
        if (cameraPermits != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),
            arrayOf(android.Manifest.permission.CAMERA), cameraPermits)
        } else {
            val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getResult.launch(takePicIntent);
        }
    }
    //Requests permission, then opens gallery
    private fun uploadFromGallery(){
        val libraryPermit =ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
        if (libraryPermit != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), libraryPermit)
        } else {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getResult.launch(galleryIntent);
        }
    }

    // Handles intent
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val value = it.data?.getStringExtra("input")
            }
        }
}