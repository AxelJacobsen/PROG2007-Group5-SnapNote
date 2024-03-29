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
    val readWrite = ReadWriteToStorage()

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

        noteListRecycler.adapter = adapter
        addNewNoteButton()

        // Fetch keys and add them as notes
        val keys = readWrite.readKeyFromStorage(this, "keys")
        addSavedNotes(keys)

        //Update
        adapter.updateData(noteList)
    }

    /**
     * Adds "New Note" item into the list
     */
    fun addNewNoteButton(){
        noteList.add(NoteListItem(
            menuItemName = "New note",
            isFirstItem = true,
            menuItemThumbnail = BitmapFactory.decodeResource(this.getResources(), R.drawable.new_note)
        ))
    }

    /**
     * Adds saved notes to noteList, so we can se them in menuActivity.
     *
     * @param keys - The name/key of the saved notes.
     */
    fun addSavedNotes(keys: List<String>){
        keys.forEach{
            noteList.add(
                NoteListItem(
                menuItemName = it,
                isFirstItem = false,
                menuItemThumbnail = readWrite.getThumbnailImageFromStorage(this, it)
            )
            )
        }
    }

    /**
     * This is the only image parsing which we could get working,
     * we tried many different solutions to this deprecated solution without any luck.
     *
     * @param requestCode - Type of request
     * @param resultCode - Result of permission request
     * @param data - Image source
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var image = ImageClass(null, null)

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

