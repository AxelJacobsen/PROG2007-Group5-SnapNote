package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class NoteActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_note)
        imageView = findViewById(R.id.noteBackground)
        //Recieves parcel and binds image data
        val image = intent.getParcelableExtra<ImageClass>("picture")

        if (image != null) {
            if (image.bitmap != null){
                imageView.setImageBitmap(image.bitmap)
            } else if (image.uri != null){
                imageView.setImageURI(image.uri)
            }
        }
        /*
        private lateinit var imageView: ImageView
        private lateinit var feedBottomSheet: ConstraintLayout
        private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
        feedBottomSheet = findViewById(R.id.coordinatorlayout)
        bottomSheetBehavior = BottomSheetBehavior.from(feedBottomSheet)
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
         */

        // Switches menu to widget menu
        val addWidgetButton = findViewById<ImageView>(R.id.iwMenuWidgets)
        val editMenuLayout = findViewById<View>(R.id.coordinatorlayout)
        val widgetMenuLayout = findViewById<View>(R.id.coordinatorlayout2)
        addWidgetButton.setOnClickListener {
            editMenuLayout.visibility = View.GONE
            widgetMenuLayout.visibility = View.VISIBLE
        }

        // define widgetlayout
        val widgetLayout = findViewById<ConstraintLayout>(R.id.widgetLayout)
        // add text
        val addTextButton = findViewById<ImageView>(R.id.iwMenuText)
        addTextButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "text", 500.0f, 300.0f, "Dynamic Text")
        }
        // add checkbox
        val addCbButton = findViewById<ImageView>(R.id.iwMenuCheckbox)
        addCbButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "checkbox", 500.0f, 300.0f, "Dynamic Text")
        }
        // add switch
        val addSwitchButton = findViewById<ImageView>(R.id.iwMenuSwitch)
        addSwitchButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "switch", 300.0f, 500.0f)
        }
        // add switch
        val addSwitchButton2 = findViewById<ImageView>(R.id.iwMenuSliderCircle)
        addSwitchButton2.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "switch", 500.0f, 300.0f, "Dynamic Text")
        }

    }

    @SuppressLint("SetTextI18n")
    private fun createButtonDynamically(
        mainLayout: ConstraintLayout,
        id : Int,
        type : String,
        posx : Float,
        posy : Float,
        text : String = "",
        red: Int = 1,
        green: Int = 1,
        blue: Int = 1
    ) {
        val dynamicElement: TextView?
        // creating the button
        if (type == "text")
            dynamicElement = TextView(this)
        else if (type == "checkbox")
            dynamicElement = CheckBox(this)
        else if (type == "switch")
            dynamicElement = Switch(this)
        else if (type == "checkbox")
            dynamicElement = CheckBox(this)
        else
            dynamicElement = TextView(this)


        // setting layout_width and layout_height using layout parameters
        dynamicElement.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        dynamicElement.x = posx
        dynamicElement.y = posy
        if (text != "")
            dynamicElement.text = text
        dynamicElement.id = id

        val radius = 15
        val strokeWidth = 2
        val gradientDrawable = GradientDrawable()
        if (red != 1 && green != 1 && blue != 1) {
            val color = Color.rgb(red, green, blue)
            gradientDrawable.setColor(color)
            gradientDrawable.setStroke(strokeWidth, color)
        }
        gradientDrawable.cornerRadius = radius.toFloat()
        dynamicElement.background = gradientDrawable

        // add Button to LinearLayout
        mainLayout.addView(dynamicElement)
    }
}
