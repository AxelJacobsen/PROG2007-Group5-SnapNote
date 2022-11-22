package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlin.math.*

/**
 * Class for drawing strokes onto a Canvas.
 */
class DrawingCanvas : View {
    private var strokes: ArrayList<Stroke> = ArrayList()
    private var cacheCanvas: Canvas? = null
    private var cacheBitmap: Bitmap? = null

    // Color wheel
    private var colorWheelEnabled: Boolean = true
    private var colorWheel: Bitmap? = null
    private var colorWheelR: Int    = -1
    private var colorWheelX: Float  = -1f
    private var colorWheelY: Float  = -1f

    // Current stroke information
    private var drawing: Boolean = false
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
        // Init canvas bitmap
        cacheBitmap = Bitmap.createBitmap(1080, 2440, Bitmap.Config.ARGB_8888)
        cacheCanvas = Canvas()
        cacheCanvas!!.setBitmap(cacheBitmap)

        // Init default paint
        strokePaint.color         = Color.RED
        strokePaint.isAntiAlias   = true
        strokePaint.style         = Paint.Style.STROKE
        strokePaint.strokeWidth   = 50f
        strokePaint.strokeJoin    = Paint.Join.ROUND
        strokePaint.strokeCap     = Paint.Cap.ROUND

        // Init colorwheel
        generateColorWheel(150f, 150f, 250)
    }

    /**
     * Undoes the last stroke.
     */
    fun undo() {
        if (strokes.size <= 0) return
        strokes.removeLast()

        // Clear cache and request redraw
        for (stroke in strokes) {
            stroke.addedToCache = false
        }
        cacheCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        invalidate()
    }

    /**
     * Sets the brush size.
     */
    fun setBrushSize(size: Float) {
        strokePaint.strokeWidth = size
    }

    /**
     * Changes the brush mode.
     * (If its in paint-mode, set to erase. Otherwise, set to paint-mode.)
     */
    fun changeBrushMode() {
        if (strokeType == StrokeType.PAINT) {
            strokeType = StrokeType.ERASE
            strokePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        } else {
            strokeType = StrokeType.PAINT
            strokePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
        }
    }

    /**
     * Generates the colorwheel at the given position.
     * x and y can be changed later without calling this function by changing [colorWheelX] and [colorWheelY].
     */
    private fun generateColorWheel(x: Float, y: Float, radius: Int) {
        var bitmap: Bitmap = Bitmap.createBitmap(radius*2, radius*2, Bitmap.Config.ARGB_8888)

        for (yLocal in -radius until radius) {
            for (xLocal in -radius until radius) {
                val dist = sqrt((xLocal*xLocal + yLocal*yLocal).toDouble())
                if (dist > radius) continue

                // Calculate HSV
                val h   = (atan2(yLocal.toDouble(), xLocal.toDouble()) / PI) * 180.0 + 180.0
                val s   = dist / radius
                val v   = 1.0
                val hsv = floatArrayOf(h.toFloat(), s.toFloat(), v.toFloat())

                // Push pixel to bitmap
                val col = Color.HSVToColor(hsv)
                bitmap[xLocal+radius, yLocal+radius] = col
            }
        }

        // Return
        colorWheel = bitmap
        colorWheelR = radius
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
        if (canvas == null || cacheCanvas == null) return

        canvas.drawBitmap(cacheBitmap!!, Matrix(), null)

        // Apply each stroke onto the canvas
        for (stroke in strokes) {
            // If the stroke isn't complete yet, apply it directly to the canvas
            if (!stroke.completed && strokeType == StrokeType.PAINT) {
                canvas.drawPath(stroke.path, stroke.paint)
                continue
            }

            // Otherwise, cache it once.
            if (!stroke.addedToCache) {
                cacheCanvas!!.drawPath(stroke.path, stroke.paint)
                if (strokeType == StrokeType.PAINT) stroke.addedToCache = true
            }
        }

        // Draw colorwheel
        if (colorWheel != null) {
            var matrix = Matrix()
            matrix.setTranslate(colorWheelX,colorWheelY)
            var colorWheelPaint = Paint()
            colorWheelPaint.isAntiAlias   = true
            colorWheelPaint.style         = Paint.Style.STROKE
            canvas.drawBitmap(colorWheel!!, matrix, colorWheelPaint)
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
                var dontDraw = false

                // Check if the touch is within the color wheel, if it's enabled and generated
                if (colorWheelEnabled && colorWheel != null) {
                    val xLocal = x - colorWheelX - colorWheelR
                    val yLocal = y - colorWheelY - colorWheelR
                    val d = sqrt(xLocal*xLocal + yLocal*yLocal)
                    if (d < colorWheelR) {
                        dontDraw = true

                        // Fetch the right color from the color wheel, and change stroke color
                        val colorInt: Int = colorWheel!![(x-colorWheelX).toInt(), (y-colorWheelY).toInt()]
                        strokePaint.color = colorInt
                    }
                }

                if (!dontDraw) {
                    var strokePath: Path = Path()
                    strokePath.moveTo(x, y)
                    strokePath.lineTo(x, y)
                    strokes.add(Stroke(Paint(strokePaint), strokePath))

                    drawing = true
                }
            }

            // Touch during
            MotionEvent.ACTION_MOVE -> {
                if (drawing) {
                    var stroke: Stroke = strokes.last()
                    stroke.path.lineTo(x, y)
                }
            }

            // Touch end
            MotionEvent.ACTION_UP -> {
                if (drawing) {
                    var stroke: Stroke = strokes.last()
                    stroke.completed = true
                    drawing = false
                }
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
        val path: Path,
        var completed: Boolean = false,
        var addedToCache: Boolean = false
    )
}