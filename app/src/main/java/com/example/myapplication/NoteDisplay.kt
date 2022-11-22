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
import com.google.android.material.bottomsheet.BottomSheetBehavior

// class Displays a single recipe page, and recieves data from pracel
class NoteDisplay : AppCompatActivity() {
    //Define interactables
    // var noteNameDisplay: TextView
    lateinit var noteImageDisplay: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_note)
        //Recieve and unpack parcel
        val noteData: NoteListItem? = intent.getParcelableExtra("extraData")
        val actualData: NoteListItem = noteData!!
        //Bind intractables
        //noteNameDisplay = findViewById(R.id.noteName)
        noteImageDisplay = findViewById(R.id.noteBackground)
        //Set Data from parcel
        //noteNameDisplay.text = actualData.menuItemName
        noteImageDisplay.setImageResource(actualData.menuItemThumbnail)

        val addWidgetButton = findViewById<ImageView>(R.id.iwMenuSwitch)
        val widgetLayout = findViewById<ConstraintLayout>(R.id.widgetLayout)

        val coordlayout = findViewById<View>(R.id.coordinatorlayout)
        val coordlayout2 = findViewById<View>(R.id.coordinatorlayout2)
        addWidgetButton.setOnClickListener {
            println("FFFFFFFFFFFFFFFFFFFFUCKKKK")
            createButtonDynamically(widgetLayout, 0, "Dynamic Button", 300.0f, 500.0f)

            //val login_layout = R.id.coordinatorlayout as View //note : from import android.view.View
            coordlayout.visibility = View.GONE
            coordlayout2.visibility = View.VISIBLE
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