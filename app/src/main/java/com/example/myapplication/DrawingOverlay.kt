package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
    private lateinit var drawMenuCoordLayout: CoordinatorLayout
    private lateinit var drawMenuLayout: ConstraintLayout
    private lateinit var seekBar: SeekBar
    private lateinit var undoButton: ImageButton
    private lateinit var brushModeButton: ImageButton
    private lateinit var colorWheelButton: ImageButton
    private lateinit var exitButton: ImageButton

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
        drawMenuCoordLayout  = view.findViewById(R.id.drawMenuCoordLayout)
        drawMenuLayout  = view.findViewById(R.id.draw_menu_layout)
        seekBar         = view.findViewById(R.id.seekBar)
        undoButton      = view.findViewById(R.id.undoButton)
        brushModeButton = view.findViewById(R.id.brushModeButton)
        colorWheelButton= view.findViewById(R.id.colorWheelButton)
        exitButton      = view.findViewById(R.id.exitButton)

        // Disable canvas by default
        setCanvasUIEnabled(false)
        setCanvasEditable(false)
        drawingCanvas.setColorWheelVisible(false)

        val viewBSBehavior = BottomSheetBehavior.from(drawMenuLayout)
        viewBSBehavior.state = BottomSheetBehavior.STATE_EXPANDED

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

        // Color wheel button listener
        colorWheelButton.setOnClickListener(OnClickListener {
            drawingCanvas.setColorWheelLocked(false)
            drawingCanvas.setColorWheelVisible(!drawingCanvas.getColorWheelVisible())
            drawingCanvas.setColorWheelLocked(!drawingCanvas.getColorWheelVisible())
        })

        // On draw/stop draw listener
        drawingCanvas.setOnDrawListener { isDrawing ->
            if (isDrawing)  setCanvasUIEnabled(false)
            else            setCanvasUIEnabled(true)
        }

        // Exit button listener
        exitButton.setOnClickListener(OnClickListener {
            setCanvasEditable(false)
            setCanvasUIEnabled(false)
        })
    }

    /**
     * Sets if the canvas can be edited.
     */
    fun setCanvasEditable(editable: Boolean) {
        drawingCanvas.canEdit = editable
    }

    /**
     * Sets if the canvas' UI is enabled.
     */
    fun setCanvasUIEnabled(enabled: Boolean) {
        val visibility = if (enabled) View.VISIBLE else View.GONE
        drawMenuCoordLayout.visibility   = visibility
        drawingCanvas.setColorWheelVisible(enabled)
    }

    /**
     * Gets the drawing canvas.
     *
     * @return The drawing canvas.
     */
    fun getDrawingCanvas(): DrawingCanvas {
        return drawingCanvas
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
        val file = File(
            path,
            fileName
        )

        fOut = FileOutputStream(file)

        pictureBitmap.compress(
            Bitmap.CompressFormat.PNG,
            100,
            fOut
        )

        fOut.flush() // Not really required
        fOut.close() // do not forget to close the stream
    }

    /**
     * Loads the bitmap of the drawing canvas from file.
     */
    fun load(context: Context, key: String?) {
        //val test = context?.openFileInput("canvas.PNG")?.bufferedReader()
        val path = context.getExternalFilesDir(Environment.DIRECTORY_DCIM) ?: return
        val fileName = "$key-canvas.PNG"
        val bitmap = BitmapFactory.decodeFile(path.absolutePath + "/$fileName") ?: return
        drawingCanvas.loadBitmap(bitmap)
    }
}
