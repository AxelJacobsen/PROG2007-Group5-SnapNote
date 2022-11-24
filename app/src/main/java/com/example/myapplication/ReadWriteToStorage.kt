package com.example.myapplication

import android.content.Context
import android.content.Context.MODE_PRIVATE
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
            lines.forEach { listOfNoteFileNames.add(it) }
        }

    }

    public fun getThumbnailImageFromStorage(context: Context){

    }


}

/*
    public fun readfromStorage(context: Context): List<Pair<String, String>> {
        var nameAndContent= mutableListOf<Pair<String, String>>()
        var files: Array<String> = context.fileList()
        files.forEach {

            val text = context.openFileInput(it).bufferedReader().useLines { lines ->
                lines.fold("") { some, text ->
                    "$some$text"
                }
            }
            println(it + " - " + text)
            nameAndContent.add(Pair(it, text))

        }
        return nameAndContent
    }*/


