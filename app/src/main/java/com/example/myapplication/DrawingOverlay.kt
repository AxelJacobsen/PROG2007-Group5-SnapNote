package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

/**
 * A simple [Fragment] subclass for drawing freehand.
 * Use the [DrawingOverlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawingOverlay : Fragment() {
    private lateinit var CanvasImageView: ImageView

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
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views and holders
        CanvasImageView = view.findViewById(R.id.canvas_iv)

        // Set canvas to be setup once the layout is ready
        view.post { setupCanvas(view) }
    }

    /**
     * Sets up the canvas to draw on.
     */
    private fun setupCanvas(
        view: View
    ) {
        // Set up canvas
        var bitmap: Bitmap = Bitmap.createBitmap(CanvasImageView.width, CanvasImageView.height, Bitmap.Config.ARGB_8888)

        var paint: Paint    = Paint()
        paint.color         = Color.RED
        paint.isAntiAlias   = true
        paint.style         = Paint.Style.STROKE
        paint.strokeWidth   = 5f

        var canvas: Canvas = Canvas(bitmap)
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, bitmap.width / 2f, paint)

        // Paste bitmap onto canvas' ImageView
        CanvasImageView.setImageBitmap(bitmap)
    }
}