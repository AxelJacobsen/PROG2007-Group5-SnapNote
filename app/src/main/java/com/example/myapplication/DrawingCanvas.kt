package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.get
import androidx.core.graphics.set
import kotlin.math.*

/**
 * Class for drawing onto a Canvas.
 */
class DrawingCanvas : View {
    // Public
    var canEdit: Boolean = true

    // Buffers
    private var strokes: ArrayList<Stroke> = ArrayList()
    private var cacheCanvas: Canvas? = null
    private var cacheBitmap: Bitmap? = null

    private var loadedCanvas: Canvas? = null
    private var loadedBitmap: Bitmap? = null
    private var loadedBitmapOriginal: Bitmap? = null

    // Color wheel
    private var colorWheelEnabled: Boolean = true
    private var colorWheelVisible: Boolean = true
    private var colorWheelLocked: Boolean = false
    private var colorWheel: Bitmap? = null
    private var colorWheelR: Int    = -1
    private var colorWheelX: Float  = -1f
    private var colorWheelY: Float  = -1f

    // Current stroke information
    private var drawing: Boolean = false
    private var strokePaint: Paint = Paint()
    private var strokeType: StrokeType = StrokeType.PAINT

    // Callbacks / onEvents
    private lateinit var onDrawCallback: (isDrawing: Boolean) -> Unit

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

        // Request redraw of each stroke
        for (stroke in strokes) {
            stroke.addedToCache = false
        }

        // Blank out cache and reset loaded canvas
        cacheCanvas?.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        if (loadedBitmap != null && loadedBitmapOriginal != null) {
            loadedBitmap = loadedBitmapOriginal!!.copy(loadedBitmapOriginal!!.config, true)
            loadedCanvas?.setBitmap(loadedBitmap)
        }

        invalidate()
    }

    /**
     * Sets the brush size.
     *
     * @param size - The new brush size.
     */
    fun setBrushSize(size: Float) {
        strokePaint.strokeWidth = size
    }

    /**
     * Changes the brush mode.
     * (If its in paint-mode, set to erase. Otherwise, set to paint-mode)
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
     * Gets the canvas' bitmap as it's displayed.
     * Does this by merging all buffers into one canvas, then returning that.
     *
     * @return A copy of the canvas' bitmap as it's displayed.
     */
    fun getBitmap(): Bitmap? {
        if (cacheBitmap == null && loadedBitmap == null) return null

        // Create copies of all buffers
        val cacheBitmapCopy  = if (cacheBitmap != null)  cacheBitmap!!.copy(cacheBitmap!!.config, true) else null
        val loadedBitmapCopy = if (loadedBitmap != null) loadedBitmap!!.copy(loadedBitmap!!.config, true) else null
        val copyPropsFrom    = cacheBitmapCopy ?: loadedBitmapCopy

        // Draw the buffer contents on top of each other
        var mergedBitmap = Bitmap.createBitmap(copyPropsFrom!!.width, copyPropsFrom!!.height, copyPropsFrom!!.config)
        var mergeCanvas  = Canvas(mergedBitmap)
        if (cacheBitmapCopy != null)    mergeCanvas.drawBitmap(cacheBitmapCopy!!, Matrix(), null)
        if (loadedBitmapCopy != null)   mergeCanvas.drawBitmap(loadedBitmapCopy!!, Matrix(), null)

        // Recycle the copies and return
        cacheBitmapCopy?.recycle()
        loadedBitmapCopy?.recycle()
        return mergedBitmap
    }

    /**
     * Loads a bitmap.
     * Replaces the previously loaded bitmap, if it exists.
     *
     * @param bitmap - The bitmap to load data from.
     */
    fun loadBitmap(bitmap: Bitmap) {
        loadedBitmapOriginal = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        loadedBitmap = loadedBitmapOriginal!!.copy(Bitmap.Config.ARGB_8888, true)
        loadedCanvas = Canvas()
        loadedCanvas?.setBitmap(loadedBitmap)

        invalidate()
    }

    /**
     * Sets the on-draw-listener.
     * This function is automatically invoked whenever the user starts/stops drawing.
     * The variable `isDrawing` represents whether or not the user has started/stopped drawing.
     *
     * @param callback - The function. Takes a boolean, returns nothing.
     */
    fun setOnDrawListener(callback: (isDrawing: Boolean) -> Unit) {
        onDrawCallback = callback
    }

    /**
     * Sets colorwheel lock.
     * A locked colorwheel cant be altered.
     *
     * @param locked - True to lock the colorwheel, false to unlock.
     */
    fun setColorWheelLocked(locked: Boolean) {
        colorWheelLocked = locked
    }

    /**
     * Gets colorwheel lock.
     *
     * @return True if the colorwheel is locked.
     */
    fun getColorWheelLocked(): Boolean {
        return colorWheelLocked
    }

    /**
     *  Sets visibility of the colorwheel.
     *  Can't be done if it's locked.
     *
     *  @param visible - True if the colorwheel should be visible, false otherwise.
     */
    fun setColorWheelVisible(visible: Boolean) {
        if (colorWheelLocked) return
        colorWheelVisible = visible
        invalidate()
    }

    /**
     * Gets if the colorwheel is visible.
     *
     * @return True if the colorwheel is visible, false otherwise.
     */
    fun getColorWheelVisible(): Boolean {
        return colorWheelVisible
    }

    /**
     * Generates the colorwheel at the given position.
     * x and y can be changed later without calling this function by changing [colorWheelX] and [colorWheelY].
     *
     * @param x - Horizontal position, left to right.
     * @param y - Vertical position, top to bottom.
     * @param radius - Radius of the colorwheel.
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

        if (loadedBitmap != null) canvas.drawBitmap(loadedBitmap!!, Matrix(), null)
        canvas.drawBitmap(cacheBitmap!!, Matrix(), null)

        // Apply each stroke onto the canvas
        for (stroke in strokes) {
            // If the stroke isn't complete yet, apply it directly to the canvas temporarily
            if (!stroke.completed && strokeType == StrokeType.PAINT) {
                canvas.drawPath(stroke.path, stroke.paint)
                continue
            }

            // Otherwise, cache it semi-permanently once.
            if (!stroke.addedToCache) {
                cacheCanvas!!.drawPath(stroke.path, stroke.paint)
                if (strokeType == StrokeType.PAINT) stroke.addedToCache = true

                // Erase from loaded canvas directly, as it's restored completely on undo.
                else if (loadedCanvas != null) loadedCanvas!!.drawPath(stroke.path, stroke.paint)
            }
        }

        // Draw colorwheel
        if (colorWheel != null && colorWheelVisible) {
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
        if (!canEdit) return false
        if (event == null) return false

        val action: Int = event.action
        val x: Float = event.x
        val y: Float = event.y

        when (action) {
            // Touch begin
            MotionEvent.ACTION_DOWN -> {
                var dontDraw = false

                // Check if the touch is within the color wheel, if it's enabled and generated
                if (colorWheelEnabled && colorWheel != null && colorWheelVisible) {
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

                    if (!drawing) {
                        drawing = true
                        onDrawCallback(drawing)
                    }
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
                    if (drawing) {
                        drawing = false
                        onDrawCallback(drawing)
                    }
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
    private data class Stroke(
        val paint: Paint,
        val path: Path,
        var completed: Boolean = false,
        var addedToCache: Boolean = false,
        var addedToLoaded: Boolean = false
    )
}