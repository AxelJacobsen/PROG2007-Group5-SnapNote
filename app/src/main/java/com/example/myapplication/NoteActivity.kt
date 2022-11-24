package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Environment
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myapplication.databinding.ActivityDisplayNoteBinding
import com.example.myapplication.databinding.MenuEditBinding
import com.example.myapplication.databinding.MenuViewBinding
import com.example.myapplication.databinding.MenuWidgetsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.*


class NoteActivity : AppCompatActivity() {

    private lateinit var activityBinding : ActivityDisplayNoteBinding
    private lateinit var mViewBinding : MenuViewBinding
    private lateinit var mEditBinding : MenuEditBinding
    private lateinit var mWidgetsBinding : MenuWidgetsBinding
    private var mDynamicElements = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityDisplayNoteBinding.inflate(layoutInflater)
        val view = activityBinding.root
        mViewBinding = MenuViewBinding.bind(view)
        mEditBinding = MenuEditBinding.bind(view)
        mWidgetsBinding = MenuWidgetsBinding.bind(view)
        setContentView(view)

        //Recieves parcel and binds image data
        val image = intent.getParcelableExtra<ImageClass>("picture")

        if (image != null) {
            if (image.bitmap != null){
                activityBinding.noteBackground.setImageBitmap(image.bitmap)
            } else if (image.uri != null){
                activityBinding.noteBackground.setImageURI(image.uri)
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

        //Returns the user to the main menu
        mViewBinding.ivMenuBackArrow.setOnClickListener {
            activityBinding.editMenuCoordLayout.visibility = View.GONE
            activityBinding.widgetMenuCoordLayout.visibility = View.GONE
            activityBinding.viewMenuCoordLayout.visibility = View.GONE
            // TODO: SaveWidgets()
            startActivity(Intent(this, MenuActivity::class.java))
        }

        mEditBinding.ivEditMenuBackArrow.setOnClickListener {
            if (activityBinding.noteBackground.scaleType == ImageView.ScaleType.FIT_CENTER) {
                activityBinding.noteBackground.scaleType = ImageView.ScaleType.CENTER_CROP
            } else {
                activityBinding.noteBackground.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        }
        
        mEditBinding.ivEditMenuBackArrow.setOnClickListener {
            activityBinding.editMenuCoordLayout.visibility = View.GONE
            activityBinding.widgetMenuCoordLayout.visibility = View.GONE
            activityBinding.viewMenuCoordLayout.visibility = View.VISIBLE

        }

        mWidgetsBinding.ivWidgetMenuBackArrow.setOnClickListener {
            activityBinding.editMenuCoordLayout.visibility = View.VISIBLE
            activityBinding.widgetMenuCoordLayout.visibility = View.GONE
            activityBinding.viewMenuCoordLayout.visibility = View.GONE
        }

        mViewBinding.ivMenuEdit.setOnClickListener {
            activityBinding.editMenuCoordLayout.visibility = View.VISIBLE
            activityBinding.widgetMenuCoordLayout.visibility = View.GONE
            activityBinding.viewMenuCoordLayout.visibility = View.GONE
        }

        mEditBinding.ivMenuWidgets.setOnClickListener {
            activityBinding.editMenuCoordLayout.visibility = View.GONE
            activityBinding.widgetMenuCoordLayout.visibility = View.VISIBLE
            activityBinding.viewMenuCoordLayout.visibility = View.GONE
        }

        // Define widgetlayout which we will add widgets to
        // add text
        mEditBinding.ivMenuText.setOnClickListener {
            createWidgetDynamically(activityBinding.widgetLayout, 0, "text", 500.0f, 300.0f, "Dynamic Text")
        }

        // add checkbox
        mWidgetsBinding.ivMenuCheckbox.setOnClickListener {
            createWidgetDynamically(activityBinding.widgetLayout, 0, "checkbox", 500.0f, 300.0f, "Dynamic Text")
        }

        // add switch
        mWidgetsBinding.ivMenuSwitch.setOnClickListener {
            createWidgetDynamically(activityBinding.widgetLayout, 0, "switch", 300.0f, 500.0f)
        }

        // add switch
        mWidgetsBinding.ivMenuSliderCircle.setOnClickListener {
            createWidgetDynamically(activityBinding.widgetLayout, 0, "switch", 500.0f, 300.0f, "Dynamic Text")
        }

        // add drawing
        activityBinding.drawingOverlay.visibility = View.GONE
        mEditBinding.ivMenuDraw.setOnClickListener {
            val editBSBehavior = BottomSheetBehavior.from(editBottomSheet)
            editBSBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            // TODO: DRAWING GETS DELETED WHEN YOU REOPEN DRAW.
            //  move supportFragManger out of onClick and fix issue,
            //  drawing menu and interaction doesn't gets started on re-drawing.
            supportFragmentManager.beginTransaction().
            replace(R.id.drawingOverlay, DrawingOverlay()).commit()
            activityBinding.drawingOverlay.visibility = View.VISIBLE
        }

        // Save document
        mViewBinding.ivMenuSave.setOnClickListener(OnClickListener {
            showInputDialog()
            //DETTE BLIR GJORT ASYNC BTW, omdu vil calle en funksjon ikke gjør det sånn her
            //HA ER 3. funksjon som KALLES AV POSITIVE BUTTON
            // blablabla
        })
    }

    /**
     * Requests a filename from the user.
     *
     * @return The filename given, or null if none.
     */
    private fun showInputDialog() {
        var builder = AlertDialog.Builder(this);
        builder.setTitle("Input note name")
        var inputField = EditText(this)
        inputField.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(inputField)
            .setPositiveButton("Save") { _, _ ->
                //KALL FUNKSJONEN DU VIL TRIGGRE HER
                var outString = inputField.text.toString()
                if (outString == ""){
                    Toast.makeText(this, "Saving note with default name", Toast.LENGTH_SHORT).show()
                }
                //saveNote(inputField.text.toString())
        }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
        }
        builder.show()
    }

    private fun createWidgetDynamically(
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
        mDynamicElements["x"] = posx.toString()
        mDynamicElements["y"] = posy.toString()
        mDynamicElements["id"] = id.toString()

        mainLayout.addView(dynamicElement)
    }

    private fun saveProps(key: String?, props: Map<String, String>) {
        // Concatenate props into a tangible string
        var str: String = ""
        for (k: String in props.keys) {
            val v = props[k]
            str += "$k:$v\n"
        }

        // Save that string to file
        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DCIM)
            val fileName = "$key-props.txt"

            var fOut: OutputStream? = null
            val file = File(
                path,
                fileName
            )
            fOut = FileOutputStream(file)
            fOut.write(str.toByteArray())

            fOut.flush()
            fOut.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadProps(key: String?): Map<String, String>? {
        var str: String = ""

        try {
            val path = getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: return null
            val fileName = "$key-props.txt"
            val file = File(
                path,
                fileName
            )

            val inputStream       = FileInputStream(file)
            val inputStreamReader = InputStreamReader(inputStream)
            val bufferedReader    = BufferedReader(inputStreamReader)

            var inString: String? = ""
            val stringBuilder = StringBuilder()

            while ( inString != null ) {
                inString = bufferedReader.readLine()
                if (inString == null) break
                stringBuilder.append("\n").append(inString)
            }

            inputStream.close()
            str = stringBuilder.toString()
        } catch (e: FileNotFoundException) {
            Log.e("NoteActivity","File not found: ${e.toString()}")
        } catch (e: IOException) {
            Log.e("NoteActivity","Could not read file: ${e.toString()}")
        }

        Log.d("String: ", str)
        return null
    }
}
