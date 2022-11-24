package com.example.myapplication

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.view.View
import java.io.FileOutputStream

class ReadWriteToStorage() {

    public fun writetoStorage(context: Context, filename: String, filecontent: String){
        context.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(filecontent.toByteArray())
        }
    }

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
    }


}