package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


/**
 * A simple [Fragment] subclass for drawing freehand.
 * Use the [DrawingOverlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawingOverlay : Fragment() {
    private lateinit var drawingCanvas: DrawingCanvas
    private lateinit var seekBar: SeekBar
    private lateinit var undoButton: ImageButton
    private lateinit var brushModeButton: ImageButton

    // TEMP
    private lateinit var saveButton: ImageButton
    private lateinit var loadButton: ImageButton

    /**
     * When the fragment is created.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    /**
     * When the fragment's view is inflated.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawing_overlay, container, false)
    }

    /**
     * After [onCreateView].
     * Find views by id here.
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views and holders
        drawingCanvas   = view.findViewById(R.id.drawingCanvas)
        seekBar         = view.findViewById(R.id.seekBar)
        undoButton      = view.findViewById(R.id.undoButton)
        brushModeButton = view.findViewById(R.id.brushModeButton)

        // TEMP
        saveButton = view.findViewById(R.id.saveButton)
        loadButton = view.findViewById(R.id.loadButton)

        // Brush size listener
        drawingCanvas.setBrushSize(seekBar.progress.toFloat())
        seekBar.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    // Set size of brush
                    drawingCanvas.setBrushSize(seekBar.progress.toFloat())
                }
            }

            // Handle Seekbar touch events.
            v.onTouchEvent(event)
            true
        })

        // Undo button listener
        undoButton.setOnClickListener(OnClickListener {
            drawingCanvas.undo()
        })

        // Change brush mode listener
        brushModeButton.setOnClickListener(OnClickListener {
            drawingCanvas.changeBrushMode()
        })

        // TEMP
        saveButton.setOnClickListener(OnClickListener {
            save(null)
        })

        loadButton.setOnClickListener(OnClickListener {
            load(null)
        })

        // On draw/stop draw listener
        drawingCanvas.setOnDrawListener { isDrawing ->
            if (isDrawing) {
                seekBar.visibility          = View.GONE
                undoButton.visibility       = View.GONE
                brushModeButton.visibility  = View.GONE
                saveButton.visibility       = View.GONE
                loadButton.visibility       = View.GONE
                drawingCanvas.setColorWheelVisible(false)
            } else {
                seekBar.visibility          = View.VISIBLE
                undoButton.visibility       = View.VISIBLE
                brushModeButton.visibility  = View.VISIBLE
                saveButton.visibility       = View.VISIBLE
                loadButton.visibility       = View.VISIBLE
                drawingCanvas.setColorWheelVisible(true)
            }
        }
    }

    /**
     * Saves the bitmap of the drawing canvas to file.
     */
    fun save(key: String?) {
        val pictureBitmap = drawingCanvas.getBitmap() ?: return

        //val path = Environment.getExternalStorageDirectory().toString() // Illegal?
        val path = activity?.getExternalFilesDir(Environment.DIRECTORY_DCIM)
        val fileName = "$key-canvas.PNG"

        var fOut: OutputStream? = null
        val counter = 0
        val file = File(
            path,
            fileName
        ) // the File to save , append increasing numeric counter to prevent files from getting overwritten.

        fOut = FileOutputStream(file)

        pictureBitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            fOut
        ) // saving the Bitmap to a file compressed as a JPEG with 85% compression rate

        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream
    }

    /**
     * Loads the bitmap of the drawing canvas from file.
     */
    fun load(key: String?) {
        //val test = context?.openFileInput("canvas.PNG")?.bufferedReader()
        val path = activity?.getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: return
        val fileName = "$key-canvas.PNG"
        val bitmap = BitmapFactory.decodeFile(path.absolutePath + "/$fileName") ?: return
        drawingCanvas.loadBitmap(bitmap)
    }
}