package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity() {
    lateinit var noteListRecycler: RecyclerView

    val noteList = mutableListOf<NoteListItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        //Bind interactables
        noteListRecycler = findViewById(R.id.recyclerMenuList)
        noteListRecycler.layoutManager = GridLayoutManager(this, 2);
        //Set adapter
        val adapter = MenuAdapter(noteList){ outItem, _ ->
            //Starts ItemDisplay class with a parcelable of the item clicked
            startActivity(Intent(this, NoteDisplay::class.java).putExtra("extraData", outItem))
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
            menuItemThumbnail = R.drawable.new_note
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