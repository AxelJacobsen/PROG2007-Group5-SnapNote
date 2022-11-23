package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Controls interaction with the main menu
 */
class MenuAdapter(var dataList: List<NoteListItem>, val onClick:(NoteListItem, Int)->Unit): RecyclerView.Adapter<MenuAdapter.ItemViewHolder>() {
    //Create bindings for clicked object
    inner class ItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val itemName: TextView
        val itemImage: ImageView
        init {
            itemName = view.findViewById(R.id.menuItemName)
            itemImage = view.findViewById(R.id.menuThumbnail)
        }
    }

    //Creates a viewholder to wrap the item recieved
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.content_note_list_item, parent, false)
        return  ItemViewHolder(view)
    }

    //Gets data from item and sends it to where it was originally clicked
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataList[holder.adapterPosition]
        holder.itemName.text = item.menuItemName
        holder.itemImage.setImageResource(item.menuItemThumbnail)
        holder.itemImage.setOnClickListener { compoundButton ->
            onClick(item, holder.adapterPosition)
        }
    }
    //Returns size of datalist
    override fun getItemCount(): Int {
        return  dataList.size
    }

    //Updates list with new list
    fun updateData(updatedDataList: List<NoteListItem>){
        dataList = updatedDataList
        notifyDataSetChanged()
    }
}