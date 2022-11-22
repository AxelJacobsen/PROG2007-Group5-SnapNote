package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * class Displays a single recipe page, and recieves data from pracel
 */
class NoteDisplay : AppCompatActivity() {
    //Define interactables
    lateinit var noteImageDisplay: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_note)
        //Recieve and unpack parcel
        val noteData: NoteListItem? = intent.getParcelableExtra("extraData")
        val actualData: NoteListItem = noteData!!
        //Bind intractables
        noteImageDisplay = findViewById(R.id.noteBackground)
        //Set Data from parcel
        noteImageDisplay.setImageResource(actualData.menuItemThumbnail)

        val addWidgetButton = findViewById<ImageView>(R.id.iwMenuSwitch)
        val widgetLayout = findViewById<ConstraintLayout>(R.id.widgetLayout)

        val editMenuLayout = findViewById<View>(R.id.coordinatorlayout)
        val widgetMenuLayout = findViewById<View>(R.id.coordinatorlayout2)
        addWidgetButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "Dynamic Button", 300.0f, 500.0f)
            editMenuLayout.visibility = View.GONE
            widgetMenuLayout.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun createButtonDynamically(
        mainLayout: ConstraintLayout,
        id : Int,
        text : String,
        posx : Float,
        posy : Float,
        red: Int = 255,
        green: Int = 255,
        blue: Int = 255
    ) {
        // creating the button
        val dynamicButton = Button(this)
        // setting layout_width and layout_height using layout parameters
        dynamicButton.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        dynamicButton.x = posx
        dynamicButton.y = posy
        dynamicButton.text = text
        dynamicButton.id = id

        val color = Color.rgb(red, green, blue) //red for example
        val radius = 15 //radius will be 5px
        val strokeWidth = 2
        val gradientDrawable = GradientDrawable()
        gradientDrawable.setColor(color)
        gradientDrawable.cornerRadius = radius.toFloat()
        gradientDrawable.setStroke(strokeWidth, color)
        dynamicButton.background = gradientDrawable

        // add Button to LinearLayout
        mainLayout.addView(dynamicButton)
    }
}