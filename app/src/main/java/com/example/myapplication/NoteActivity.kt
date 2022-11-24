package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior

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

        // Set state of widget menu to Expanded
        val viewBottomSheet = findViewById<ConstraintLayout>(R.id.view_bottom_sheet_layout)
        val viewBSBehavior = BottomSheetBehavior.from(viewBottomSheet)
        viewBSBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Set state of edit menu to Expanded
        val editBottomSheet = findViewById<ConstraintLayout>(R.id.edit_bottom_sheet_layout)
        val editBSBehavior = BottomSheetBehavior.from(editBottomSheet)
        editBSBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Set state of widget menu to Expanded
        val widgetBottomSheet = findViewById<ConstraintLayout>(R.id.widget_bottom_sheet_layout)
        val widgetBSBehavior = BottomSheetBehavior.from(widgetBottomSheet)
        widgetBSBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        // Switches menu to widget menu
        val addWidgetButton = findViewById<ImageView>(R.id.ivMenuWidgets)
        val returnToMainMenuButton = findViewById<ImageView>(R.id.ivMenuBackArrow)
        val returnToEditButton  = findViewById<ImageView>(R.id.ivWidgetMenuBackArrow)
        val returnToViewButton = findViewById<ImageView>(R.id.ivEditMenuBackArrow)
        val openEditMenu = findViewById<ImageView>(R.id.ivMenuEdit)
        val menu_view = findViewById<View>(R.id.viewMenuCoordLayout)
        val editMenuLayout = findViewById<View>(R.id.editMenuCoordLayout)
        val widgetMenuLayout = findViewById<View>(R.id.widgetMenuCoordLayout)

        val toggleScreenFit = findViewById<View>(R.id.ivEditToggleScreen)

        //Returns the user to the main menu
        returnToMainMenuButton.setOnClickListener {
            editMenuLayout.visibility = View.GONE
            widgetMenuLayout.visibility = View.GONE
            menu_view.visibility = View.GONE
            // TODO: SaveWidgets()
            startActivity(Intent(this, MenuActivity::class.java))
        }

        toggleScreenFit.setOnClickListener {
            if (imageView.scaleType == ImageView.ScaleType.FIT_CENTER) {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }

        returnToViewButton.setOnClickListener {
            editMenuLayout.visibility = View.GONE
            widgetMenuLayout.visibility = View.GONE
            menu_view.visibility = View.VISIBLE
        }

        returnToEditButton.setOnClickListener {
            editMenuLayout.visibility = View.VISIBLE
            widgetMenuLayout.visibility = View.GONE
            menu_view.visibility = View.GONE
        }

        openEditMenu.setOnClickListener {
            editMenuLayout.visibility = View.VISIBLE
            widgetMenuLayout.visibility = View.GONE
            menu_view.visibility = View.GONE
        }

        addWidgetButton.setOnClickListener {
            editMenuLayout.visibility = View.GONE
            widgetMenuLayout.visibility = View.VISIBLE
            menu_view.visibility = View.GONE
        }

        // Define widgetlayout which we will add widgets to
        val widgetLayout = findViewById<ConstraintLayout>(R.id.widgetLayout)
        // add text
        val addTextButton = findViewById<ImageView>(R.id.ivMenuText)
        addTextButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "text", 500.0f, 300.0f, "Dynamic Text")
        }

        // add checkbox
        val addCbButton = findViewById<ImageView>(R.id.ivMenuCheckbox)
        addCbButton.setOnClickListener {
            println("widgeecheckboxcheckboxcheckboxcheckboxeeeeet")
            createButtonDynamically(widgetLayout, 0, "checkbox", 500.0f, 300.0f, "Dynamic Text")
        }

        // add switch
        val addSwitchButton = findViewById<ImageView>(R.id.ivMenuSwitch)
        addSwitchButton.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "switch", 300.0f, 500.0f)
        }

        // add switch
        val addSwitchButton2 = findViewById<ImageView>(R.id.ivMenuSliderCircle)
        addSwitchButton2.setOnClickListener {
            createButtonDynamically(widgetLayout, 0, "switch", 500.0f, 300.0f, "Dynamic Text")
        }
    }

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
        var dynamicElement: TextView?
        // creating the button
        if (type == "text")
            dynamicElement = TextView(this)
        else if (type == "checkbox") {
            println("checkbox")
            dynamicElement = CheckBox(this)
        } else if (type == "switch")
            dynamicElement = Switch(this)
        else
            dynamicElement = TextView(this)

        // setting layout_width and layout_height using layout parameters
        dynamicElement.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        if (text != "")
            dynamicElement.text = text

        dynamicElement.x = posx
        dynamicElement.y = posy
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
