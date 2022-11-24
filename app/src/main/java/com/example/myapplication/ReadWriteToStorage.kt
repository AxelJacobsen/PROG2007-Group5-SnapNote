package com.example.myapplication

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.view.View
import java.io.FileOutputStream

class ReadWriteToStorage() {

    public var listOfNoteFileNames = mutableListOf<String>()

    public fun writeKeyfileToStorage(context: Context, filename: String){
        var file: String = ""
        listOfNoteFileNames.forEach{
            file += "$it\n"
        }

        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(file.toByteArray())
        }
    }

    public fun readKeyFromStorage(context: Context, filename: String){
        context.openFileInput(filename).bufferedReader().useLines { lines ->
            lines.forEach {
                println(it)
                listOfNoteFileNames.add(it)}
        }
    }

    public fun getThumbnailImageFromStorage(context: Context, filename: String): Bitmap?{
        // Load background - TEMP, MOVE THIS TO PARENT ACTIVITY
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: return null
        val fileName = "$filename-background.PNG"
        return BitmapFactory.decodeFile(path.absolutePath + "/$fileName")
    }

}


