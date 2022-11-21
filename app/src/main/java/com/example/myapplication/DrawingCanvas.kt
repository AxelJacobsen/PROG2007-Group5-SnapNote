package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import kotlin.math.log

/**
 * Class for drawing strokes onto a Canvas.
 */
class DrawingCanvas : View {
    private var strokes: ArrayList<Stroke> = ArrayList()

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