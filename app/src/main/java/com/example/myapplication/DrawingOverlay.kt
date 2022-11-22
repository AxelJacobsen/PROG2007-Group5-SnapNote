package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnClickListener
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.fragment.app.Fragment

/**
 * A simple [Fragment] subclass for drawing freehand.
 * Use the [DrawingOverlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawingOverlay : Fragment() {
    private lateinit var drawingCanvas: DrawingCanvas
    private lateinit var seekBar: SeekBar
    private lateinit var undoButton: ImageButton

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

        // Attach listeners
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

        undoButton.setOnClickListener(OnClickListener {
            drawingCanvas.undo()
        })
    }
}