package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.set
import kotlin.math.*

/**
 * Class for drawing strokes onto a Canvas.
 */
class DrawingCanvas : View {
    private var strokes: ArrayList<Stroke> = ArrayList()

    // Color wheel
    private var colorWheel: Bitmap? = null
    private var colorWheelX: Float = -1f
    private var colorWheelY: Float = -1f

    // Current stroke information
    private var strokePaint: Paint = Paint()
    private var strokeType: StrokeType = StrokeType.PAINT

    constructor(
        ctx: Context,
        atr: AttributeSet
    ) : super(ctx, atr) {
        init()
    }

    /**
     * Initializes the member's variables.
     */
    private fun init() {
        // Init default paint
        strokePaint.color         = Color.RED
        strokePaint.isAntiAlias   = true
        strokePaint.style         = Paint.Style.STROKE
        strokePaint.strokeWidth   = 5f
        strokePaint.strokeJoin    = Paint.Join.ROUND
        strokePaint.strokeCap     = Paint.Cap.ROUND

        // Init colorwheel
        generateColorWheel(100f, 100f, 250)
    }

    /**
     * Undoes the last stroke.
     */
    fun undo() {
        if (strokes.size <= 0) return
        strokes.removeLast()
        invalidate()
    }

    fun generateColorWheel(x: Float, y: Float, radius: Int) {
        var bitmap: Bitmap = Bitmap.createBitmap(radius*2, radius*2, Bitmap.Config.ARGB_8888)

        for (yLocal in -radius until radius) {
            for (xLocal in -radius until radius) {
                val dist = sqrt((xLocal*xLocal + yLocal*yLocal).toDouble())
                if (dist > radius) continue

                // Calculate HSV
                val h   = (atan2(yLocal.toDouble(), xLocal.toDouble()) / PI) * 180.0 + 180.0
                val s   = dist / radius
                val v   = 1f
                val hsv = floatArrayOf(h.toFloat(), s.toFloat(), v.toFloat())

                // Push pixel to bitmap
                val col = Color.HSVToColor(hsv)
                bitmap[xLocal+radius, yLocal+radius] = col//Color.parseColor(color)
            }
        }

        // Return
        colorWheel = bitmap
        colorWheelX = x
        colorWheelY = y
    }

    /**
     * When the canvas is drawn to the screen.
     */
    override fun onDraw(
        canvas: Canvas?
    ) {
        super.onDraw(canvas)
        if (canvas == null) return

        // Apply each stroke onto the canvas
        for (stroke in strokes) {
            canvas.drawPath(stroke.path, stroke.paint)
        }

        // Draw colorwheel
        if (colorWheel != null) {
            var matrix = Matrix()
            matrix.setTranslate(colorWheelX,colorWheelY)
            canvas.drawBitmap(colorWheel!!, matrix, strokePaint)
        }
    }

    /**
     * When the screen is touched.
     */
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return false

        val action: Int = event.action
        val x: Float = event.x
        val y: Float = event.y

        when (action) {
            // Touch begin
            MotionEvent.ACTION_DOWN -> {
                var strokePath: Path = Path()
                strokePath.moveTo(x, y)
                strokePath.lineTo(x, y)
                strokes.add(Stroke(strokePaint, strokePath))
            }

            // Touch during
            MotionEvent.ACTION_MOVE -> {
                var stroke: Stroke = strokes.last()
                stroke.path.lineTo(x, y)
            }
        }

        invalidate() // Tell the program to redraw the view.
        return true;
    }

    /**
     * A simple enum to describe what kind of operation a stroke does.
     */
    private enum class StrokeType {
        PAINT,
        ERASE
    }

    /**
     * A simple dataclass for storing stroke information.
     */
    data class Stroke(
        val paint: Paint,
        val path: Path
    )
}