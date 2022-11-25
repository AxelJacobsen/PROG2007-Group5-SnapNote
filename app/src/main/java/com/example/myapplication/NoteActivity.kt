package com.example.myapplication

import android.R.attr.data
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
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
import kotlin.math.sqrt


class NoteActivity : AppCompatActivity() {

    private lateinit var activityBinding : ActivityDisplayNoteBinding
    private lateinit var mViewBinding : MenuViewBinding
    private lateinit var mEditBinding : MenuEditBinding
    private lateinit var mWidgetsBinding : MenuWidgetsBinding
    private var mDynamicElements = mutableListOf<View>()
    private var mProps = mutableListOf<Map<String, String>>()
    private lateinit var mBackgroundImage : ImageClass

    // Editing widgets
    private var editing: Boolean = false
    private var moving: Boolean = false
    private var moveStartX: Float? = null
    private var moveStartY: Float? = null
    private var moveOffsetX: Float? = null
    private var moveOffsetY: Float? = null
    private var widgetSelected: View? = null

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
            // Fetch bitmap if it isn't supplied
            if (image.bitmap == null) {
                val uri: Uri? = image.uri
                if (uri != null) image.bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
            }

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

        mEditBinding.ivEditToggleScreen.setOnClickListener {
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

            // make widgets tangible
            setDynamicElementsEnabled(true)
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

            // Make widgets intangible
            setDynamicElementsEnabled(false)
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
            mDrawingOverlay.getDrawingCanvas().setColorWheelLocked(true)
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
     * When the user touches the screen.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        super.onTouchEvent(event)

        // If not in edit mode, return (don't do anything special)
        if (!editing) return false
        if (event == null) return false

        val action: Int = event.action
        val x: Float = event.x
        val y: Float = event.y

        when (action) {
            // Touch begin
            MotionEvent.ACTION_DOWN -> {
                // Find the nearest dynamic element
                var closestDistSqr: Float? = null
                var closestDynamicElement: View? = null
                for (dynamicElement in mDynamicElements) {
                    var dynamicElementCoords = IntArray(2)
                    dynamicElement.getLocationOnScreen(dynamicElementCoords)
                    val dx = x - dynamicElementCoords[0]
                    val dy = y - dynamicElementCoords[1]
                    val distSqr = dx*dx + dy*dy
                    if (closestDistSqr == null || distSqr < closestDistSqr) {
                        closestDistSqr = distSqr
                        closestDynamicElement = dynamicElement
                    }
                }

                // If it's close enough, start select it
                if (closestDistSqr != null && closestDynamicElement != null && sqrt(closestDistSqr) <= 150f) {
                    widgetSelected = closestDynamicElement
                    moveStartX = widgetSelected?.x
                    moveStartY = widgetSelected?.y

                    // Calculate offset
                    var dynamicElementCoords = IntArray(2)
                    closestDynamicElement.getLocationOnScreen(dynamicElementCoords)
                    moveOffsetX = closestDynamicElement.x - dynamicElementCoords[0]
                    moveOffsetY = closestDynamicElement.y - dynamicElementCoords[1]
                }
            }

            // Touch during
            MotionEvent.ACTION_MOVE -> {
                if (widgetSelected == null) return false

                // If we've moved more than a given amount, set widget as "moving"
                val dx = x - moveStartX!!
                val dy = y - moveStartY!!
               // Log.d("dist: ", sqrt(dx*dx + dy*dy).toString())
                if (sqrt(dx*dx + dy*dy) >= 15f) {
                    moving = true
                }

                // If we're moving, move
                if (!moving) return false
                widgetSelected!!.x = x + moveOffsetX!!
                widgetSelected!!.y = y + moveOffsetY!!
            }

            // Touch end
            MotionEvent.ACTION_UP -> {
                // If we tapped, but didn't move, "delete" widget (hide it and remove from lists)
                if (!moving) {
                    widgetSelected?.visibility = View.GONE
                    mDynamicElements.remove(widgetSelected)
                }
                moving = false
                moveStartX = null
                moveStartY = null
                widgetSelected = null
                moveOffsetX = null
                moveOffsetY = null
            }
        }

        return true
    }

    /**
     * Sets if dynamic elements are enabled (possible to interact with).
     * Additionally, enables/disables editor mode.
     *
     * @param enabled - True to enable dynamic elements' interaction, false otherwise.
     */
    private fun setDynamicElementsEnabled(enabled: Boolean) {
        editing = !enabled
        for (dynamicElement in mDynamicElements) {
            dynamicElement.isEnabled = enabled
        }
    }

    /**
     * Requests a filename from the user.
     */
    private fun showInputDialog() {
        var builder = AlertDialog.Builder(this);
        builder.setTitle("Input note name")
        var inputField = EditText(this)
        //Initialize EditText
        inputField.inputType = InputType.TYPE_CLASS_TEXT

        builder.setView(inputField)
            .setPositiveButton("Save") { _, _ ->
                var outString = inputField.text.toString()
                if (outString == ""){
                    Toast.makeText(this, "Saving note with default name", Toast.LENGTH_SHORT).show()
                    outString = "new page"
                }
                saveNote(outString)
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
        saveProps(key, mProps)

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

        mProps.add(myProp)
        mDynamicElements.add(dynamicElement)
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
