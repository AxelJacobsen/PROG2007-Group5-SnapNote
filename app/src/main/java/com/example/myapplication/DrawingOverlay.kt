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
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.widget.Button
import android.widget.ImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrawingOverlay.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrawingOverlay : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var CanvasImageView: ImageView

    // Testing
    private lateinit var button: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_drawing_overlay, container, false)
    }

    /**
     * View setup.
     * Use findViewByID etc here
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fetch views and holders
        CanvasImageView = view.findViewById(R.id.canvas_iv)
        button          = view.findViewById(R.id.button)

        // Set listeners
        button.setOnClickListener {
            setupCanvas(view)
        }
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DrawingOverlay.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DrawingOverlay().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}