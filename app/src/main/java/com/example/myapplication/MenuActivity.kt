package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * This activity controls the initial menu where you select your desired note
 */
class MenuActivity : AppCompatActivity() {
    lateinit var noteListRecycler: RecyclerView

    val noteList = mutableListOf<NoteListItem>()

    val test = ReadWriteToStorage()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        //Bind interactables
        noteListRecycler = findViewById(R.id.recyclerMenuList)
        //Set gridlayout
        noteListRecycler.layoutManager = GridLayoutManager(this, 2);
        //Set adapter
        val adapter = MenuAdapter(noteList){ outItem, _ ->
            //Opens Camera button display
            if (outItem.isFirstItem) {
                val dialogManager = supportFragmentManager
                CameraButtonFragment().show(
                    dialogManager, "Camera Dialog")
            } else {
                //Initiates NoteDisplay activity with data from iten clicked
                startActivity(Intent(this, NoteActivity::class.java).putExtra("extraData", outItem))
            }
        }

        test.writeKeyfileToStorage(this, "keys")
        test.readKeyFromStorage(this,"keys")



        noteListRecycler.adapter = adapter
        addNewNoteButton()      //fill recipeList
        //addSavedNotes(test.readfromStorage(this))
        //Update
        adapter.updateData(noteList)
    }
    // Adds "New Note" item into the list
    fun addNewNoteButton(){
        noteList.add(NoteListItem(
            menuItemName = "New note",
            isFirstItem = true,
            menuItemThumbnail = BitmapFactory.decodeResource(this.getResources(), R.drawable.new_note)
        ))
    }

    fun addSavedNotes(savedNotes: List<Pair<String, String>>){
        savedNotes.forEach{
            noteList.add(
                NoteListItem(
                menuItemName = it.first,
                isFirstItem = false,
                menuItemThumbnail = null
            )
            )
        }
    }

    /**
     * This is hell, and should not work. Why it works is a mystery
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var image = ImageClass(null, null)
        println(requestCode)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data == null) {
                image.bitmap = data?.extras?.get("data") as Bitmap
            } else {
                image.uri = data?.data as Uri
            }
        }
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra("picture", image)
        startActivity(intent)
        super.onActivityResult(requestCode, resultCode, data)
    }
}

