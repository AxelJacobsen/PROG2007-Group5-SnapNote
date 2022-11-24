package com.example.myapplication

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    private var mDynamicElements = mutableListOf<Map<String, String>>()
    private lateinit var mBackgroundImage : ImageClass
    private var noteName: String? = null

    // Drawing
    private var mDrawingOverlay = DrawingOverlay()

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
        val note = intent.getParcelableExtra<NoteListItem>("extraData")
        var noteName: String? = null

        if (image != null) {
            mBackgroundImage = image
            if (image.bitmap != null){
                activityBinding.noteBackground.setImageBitmap(image.bitmap)
            } else if (image.uri != null){
                activityBinding.noteBackground.setImageURI(image.uri)
            }
        } else if (note != null){
            activityBinding.noteBackground.setImageBitmap(note.menuItemThumbnail)
            noteName = note.menuItemName
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

        // Get drawing overlay fragment
        supportFragmentManager.beginTransaction().replace(R.id.drawingOverlay, mDrawingOverlay).commit()

        // add drawing
        mEditBinding.ivMenuDraw.setOnClickListener {
            val editBSBehavior = BottomSheetBehavior.from(editBottomSheet)
            editBSBehavior.state = BottomSheetBehavior.STATE_COLLAPSED

            mDrawingOverlay.setCanvasUIEnabled(true)
            mDrawingOverlay.setCanvasEditable(true)

            // Start with all toolbars visible, and the colorwheel off
            mDrawingOverlay.getDrawingCanvas().setColorWheelVisible(false)
        }

        // Save document
        mViewBinding.ivMenuSave.setOnClickListener(OnClickListener {
            showInputDialog()
            //DETTE BLIR GJORT ASYNC BTW, omdu vil calle en funksjon ikke gjør det sånn her
            //HA ER 3. funksjon som KALLES AV POSITIVE BUTTON
            // blablabla
        })

        view.post { loadNote(noteName) }
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
                saveNote(inputField.text.toString())
        }
            .setNegativeButton("Cancel") { dialogInterface: DialogInterface, _ ->
                dialogInterface.dismiss()
        }
        builder.show()
    }

    /**
     * Saves the note under a given "key" (unique identifier).
     *
     * @param key - The key.
     */
    private fun saveNote(key: String?) {
        // Save dynamic elements
        saveProps(key, mDynamicElements)

        // Save canvas
        mDrawingOverlay.save(key)

        // Save background
        val path     = getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val fileName = "$key-background.PNG"

        var fOut: OutputStream? = null
        val file = File(
            path,
            fileName
        )

        fOut = FileOutputStream(file)

        mBackgroundImage.bitmap?.compress(
            Bitmap.CompressFormat.PNG,
            100,
            fOut
        )

        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream

        ReadWriteToStorage().addKeyToStorage(this, "keys", key!!)
    }

    /**
     * Loads the note from a given "key" (unique identifier).
     *
     * @param key - The key.
     */
    private fun loadNote(key: String?) {
        // Load dynamic elements' props
        val props = loadProps(key) ?: return

        // Recreate objects
        for (propSet in props) {
            createWidgetDynamically(
                activityBinding.widgetLayout,
                propSet["id"]?.toInt() ?: continue,
                propSet["type"] ?: continue,
                propSet["x"]?.toFloat() ?: continue,
                propSet["y"]?.toFloat() ?: continue,
                propSet["text"] ?: ""
            )
        }

        // Load canvas
        mDrawingOverlay.load(this, key)
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
        var dynamicElement: TextView? = null

        // Set up props
        var myProp      = mutableMapOf<String, String>()
        myProp["id"]    = id.toString()
        myProp["type"]  = type
        myProp["x"]     = posx.toString()
        myProp["y"]     = posx.toString()

        // Cast object dynamically and assign type-specific props
        when (type) {
            "text" -> {
                dynamicElement = TextView(this)
                myProp["text"] = text
            }

            "checkbox" -> {
                dynamicElement = CheckBox(this)
            }

            "switch" -> {
                dynamicElement = Switch(this)
            }

            else -> {
                Log.e("NoteActivity.createWidgetDynamically", "Object of type $type could not be made!")
            }
        }

        // setting layout_width and layout_height using layout parameters
        if (dynamicElement == null) return
        dynamicElement.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        )
        if (text != "")
            dynamicElement.text = text

        dynamicElement.id = id
        dynamicElement.x = posx
        dynamicElement.y = posy

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

        mDynamicElements.add(myProp)
        mainLayout.addView(dynamicElement)
    }

    /**
     * Saves a set of properties to file.
     *
     * @param key - The "key" to the file.
     * @param props - A set of properties.
     */
    private fun saveProps(key: String?, props: List<Map<String, String>>) {
        // Concatenate props into a tangible string
        var str: String = ""

        for (prop in props) {
            for (k: String in prop.keys) {
                val v = prop[k]
                str += "$k:$v\t"
            }
            str += "\n"
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

    /**
     * Loads a set of properties from file.
     *
     * @param key - The "key" to the file.
     *
     * @return The list of properties, or null if none could be found.
     */
    private fun loadProps(key: String?): List<Map<String, String>>? {
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

        var props = mutableListOf<Map<String, String>>()
        // Iterate objects
        val objectStrings = str.split("\n")
        for (objectString in objectStrings) {

            // Iterate properties
            val propSet = mutableMapOf<String, String>()

            val objectPropStrings = objectString.split("\t")
            for (objectPropString in objectPropStrings) {
                val objectPropPair = objectPropString.split(":")
                if (objectPropPair.size < 2) continue

                val objectPropKey = objectPropPair[0]
                val objectPropVal = objectPropPair[1]
                propSet[objectPropKey] = objectPropVal
            }

            props.add(propSet)
        }

        return props
    }
}
