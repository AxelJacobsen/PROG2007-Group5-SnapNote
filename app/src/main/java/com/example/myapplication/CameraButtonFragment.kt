package com.example.myapplication

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.lang.Thread.sleep

/**
 * Opens a dialog asking the user if they wish to take a picture or load from gallery
 */
class CameraButtonFragment : DialogFragment() {
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
        createPermissionRequest(android.Manifest.permission.CAMERA, 1){ gotPermission ->
            if (gotPermission){
                val takePicIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                getResult.launch(takePicIntent)
            } else {
                Toast.makeText(requireContext(), "Camera permission denied, update in device settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Requests permission for gallery use and opens the gallery
     */
    private fun uploadFromGallery(){
        createPermissionRequest(android.Manifest.permission.READ_EXTERNAL_STORAGE, 2) { gotPermission ->
            if (gotPermission) {
                val galleryIntent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                getResult.launch(galleryIntent)
            } else {
                Toast.makeText(requireContext(), "Gallery permission denied, update in device settings", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Handles launching intent
     */
    private val getResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK){
                val value = it.data?.getStringExtra("input")
            }
        }

    /**
     * Creates a permissions request and returns synchronously
     */
    private fun createPermissionRequest(perm: String, requestCode: Int, onSuccess:(Boolean)->Unit){
        val permit = context?.checkSelfPermission(perm)
        if (permit != PackageManager.PERMISSION_GRANTED) {
            activity?.requestPermissions(arrayOf(perm),requestCode)
            if (context?.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED){
                onSuccess(false)
            }
            onSuccess(true)
        }
        else onSuccess(true)
    }
}