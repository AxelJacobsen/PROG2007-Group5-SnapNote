package com.example.myapplication

import android.R.attr.button
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Color.GREEN
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


/**
 * This activity controls the initial menu where you select your desired note
 */
class MenuActivity : AppCompatActivity() {
    lateinit var noteListRecycler: RecyclerView

    val noteList = mutableListOf<NoteListItem>()

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
            startActivity(Intent(this, NoteDisplay::class.java).putExtra("extraData", outItem))
            /*if (outItem.isFirstItem) {
                val dialogManager = supportFragmentManager
                CameraButtonFragment().show(
                    dialogManager, "Camera Dialog")
            } else {
                //Initiates NoteDisplay activity with data from iten clicked
                startActivity(Intent(this, NoteDisplay::class.java).putExtra("extraData", outItem))
            }*/
        }

        noteListRecycler.adapter = adapter
        addNewNoteButton()      //fill recipeList
        //Update
        adapter.updateData(noteList)
        //Handle Searchbar, updates the filter each time a button is clicked
        /*editText.addTextChangedListener {
            adapter.updateData(noteList.filter{MenuItemData -> MenuItemData.menuItemName.lowercase().contains(editText.text.toString().lowercase())})
        }*/
    }
    // Adds "New Note" item into the list
    fun addNewNoteButton(){
        noteList.add(NoteListItem(
            menuItemName = "New note",
            isFirstItem = true,
            menuItemThumbnail = R.drawable.placeholder
        ))
        noteList.add(NoteListItem(
            menuItemName = "New note",
            isFirstItem = true,
            menuItemThumbnail = R.drawable.new_note
        ))
        noteList.add(NoteListItem(
            menuItemName = "New note",
            isFirstItem = true,
            menuItemThumbnail = R.drawable.new_note
        ))
    }
}