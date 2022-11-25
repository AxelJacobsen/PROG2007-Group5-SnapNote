package com.example.myapplication

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import java.io.FileNotFoundException

class ReadWriteToStorage() {
    /**
     * Adds a key to storage.
     *
     * @param context - The context.
     * @param filename - The file to write to.
     * @param key - The key.
     *
     * @return true if the key was successfully added, false otherwise.
     */
    public fun addKeyToStorage(context: Context, filename: String, key: String): Boolean{
        // Check if the key already is in storage, if so, return false
        val keys = readKeyFromStorage(context, filename)
        for (key_t in keys) if (key_t == key) return false

        // Otherwise, add it
        context.openFileOutput(filename, Context.MODE_APPEND).use {
            it.write("$key\n".toByteArray())
        }
        return true
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


