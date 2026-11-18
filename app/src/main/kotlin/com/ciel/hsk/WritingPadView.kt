package com.ciel.hsk

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Typeface
import android.view.MotionEvent
import android.view.View

class WritingPadView(context: Context) : View(context) {
    private val strokes = mutableListOf<MutableList<PointF>>()
    private var currentStroke: MutableList<PointF>? = null
    private var character = ""
    private var guideVisible = true

    private val inkPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#C62828")
        style = Paint.Style.STROKE
        strokeWidth = 12 * resources.displayMetrics.density
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }
    private val guidePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(48, 120, 120, 120)
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(45, 120, 120, 120)
        style = Paint.Style.STROKE
        strokeWidth = 1 * resources.displayMetrics.density
    }

    fun setCharacter(value: String) {
        character = value
        clear()
    }

    fun clear() {
        strokes.clear()
        currentStroke = null
        invalidate()
    }

    fun undo() {
        if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
        invalidate()
    }

    fun setGuideVisible(visible: Boolean) {
        guideVisible = visible
        invalidate()
    }

    fun traceScore(): Int {
        val size = 96
        val expected = renderMask(true, size)
        val actual = renderMask(false, size)
        return WritingEvaluator.traceScore(expected, actual, size, size)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, width)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val centerX = width / 2f
        val centerY = height / 2f
        canvas.drawLine(centerX, 0f, centerX, height.toFloat(), gridPaint)
        canvas.drawLine(0f, centerY, width.toFloat(), centerY, gridPaint)
        canvas.drawLine(0f, 0f, width.toFloat(), height.toFloat(), gridPaint)
        canvas.drawLine(width.toFloat(), 0f, 0f, height.toFloat(), gridPaint)
        if (guideVisible) drawGuide(canvas)
        drawInk(canvas)
    }

    private fun drawGuide(canvas: Canvas) {
        if (character.isEmpty()) return
        guidePaint.textSize = width * 0.72f
        val metrics = guidePaint.fontMetrics
        val baseline = height / 2f - (metrics.ascent + metrics.descent) / 2f
        canvas.drawText(character, width / 2f, baseline, guidePaint)
    }

    private fun drawInk(canvas: Canvas) {
        for (stroke in strokes) {
            if (stroke.isEmpty()) continue
            val path = Path()
            path.moveTo(stroke[0].x, stroke[0].y)
            for (i in 1 until stroke.size) path.lineTo(stroke[i].x, stroke[i].y)
            if (stroke.size == 1) path.lineTo(stroke[0].x + 0.1f, stroke[0].y + 0.1f)
            canvas.drawPath(path, inkPaint)
        }
    }

    private fun renderMask(guide: Boolean, size: Int): BooleanArray {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scale = size.toFloat() / width.coerceAtLeast(1)
        canvas.scale(scale, scale)
        if (guide) drawGuide(canvas) else drawInk(canvas)
        val pixels = IntArray(size * size)
        bitmap.getPixels(pixels, 0, size, 0, 0, size, size)
        bitmap.recycle()
        return BooleanArray(size * size) { Color.alpha(pixels[it]) > 32 }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                parent.requestDisallowInterceptTouchEvent(true)
                val stroke = mutableListOf(PointF(event.x, event.y))
                strokes.add(stroke)
                currentStroke = stroke
            }
            MotionEvent.ACTION_MOVE -> currentStroke?.add(PointF(event.x, event.y))
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                currentStroke = null
                parent.requestDisallowInterceptTouchEvent(false)
                performClick()
            }
        }
        invalidate()
        return true
    }

    override fun performClick(): Boolean = super.performClick()
}
