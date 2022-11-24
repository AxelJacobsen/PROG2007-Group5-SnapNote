package com.example.myapplication

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.View
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ReadWriteToStorage() {
    /**
     * Adds a key to storage.
     *
     * @param context - The context.
     * @param filename - The file to write to.
     * @param key - The key.
     */
    public fun addKeyToStorage(context: Context, filename: String, key: String){
        context.openFileOutput(filename, Context.MODE_APPEND).use {
            it.write("$key\n".toByteArray())
        }
    }

    /**
     * Reads keys from storage.
     *
     * @param context - The context.
     * @param filename - The file to read from.
     *
     * @return A list of keys.
     */
    public fun readKeyFromStorage(context: Context, filename: String): List<String> {
        val keys = mutableListOf<String>()

        try {
            context.openFileInput(filename).bufferedReader().useLines { lines ->
                lines.forEach {
                    keys.add(it)
                }
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return keys
    }

    /**
     * Gets the thumbnail of a given note from storage.
     *
     * @param context - The context.
     * @param key - The file to read from.
     *
     * @return The bitmap of the thumbnail, or null if none was found.
     */
    public fun getThumbnailImageFromStorage(context: Context, key: String?): Bitmap?{
        // Load background - TEMP, MOVE THIS TO PARENT ACTIVITY
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: return null
        val fileName = "$key-background.PNG"
        val bitmap = BitmapFactory.decodeFile(path.absolutePath + "/$fileName")
        return bitmap
    }

}


