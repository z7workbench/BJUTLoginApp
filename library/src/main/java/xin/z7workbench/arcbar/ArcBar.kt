package xin.z7workbench.arcbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos


class ArcBar(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var percent = 0F
    var range = 220F
    var text = "Text"
    var backColor = 0x888888
    var frontColor = 0x8686ff
    var stroke = 1
        set(value) {
            field = dp2px(value.toFloat())
            canvasPaint.strokeWidth = stroke.toFloat()
            refreshTheLayout()
        }
    private val tag = "arc"
    private lateinit var oval: RectF
    private val canvasPaint = Paint()
    private val textPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasPaint.strokeWidth = stroke.toFloat()
        canvasPaint.color = backColor
        canvas.drawArc(oval, 270F - range / 2, range, false, canvasPaint)
        canvasPaint.color = frontColor
        canvas.drawArc(oval, 270F - range / 2, percent * range / 100, false, canvasPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateOval()
    }

    fun refreshTheLayout() {
        invalidate()
        requestLayout()
    }

    private fun widthHeightRatio(): Float {
        val xp = paddingStart + paddingEnd
        val x = width - xp
        val angle = range / 2

        return if (angle < 90 && angle <= 180) x.toFloat() / (x * (cos(180 - angle) + 1) / 2)
        else x.toFloat() / (x / 2).toFloat()
    }
    private fun updateOval() {
        val xp = paddingLeft + paddingRight
        val yp = paddingBottom + paddingTop
        oval = RectF((paddingLeft + stroke).toFloat(), (paddingTop + stroke).toFloat(),
                (paddingLeft + (width - xp) - stroke).toFloat(),
                (paddingTop + (height - yp) - stroke).toFloat())
    }

    private fun dp2px(value: Float) = (_context.resources.displayMetrics.density * value + 0.5f).toInt()

    init {
        val typedArray = _context.theme.obtainStyledAttributes(attrs,
                R.styleable.ArcBar, 0, 0)
        percent = typedArray.getFloat(R.styleable.ArcBar_arcPercent, 0F)
        backColor = typedArray.getColor(R.styleable.ArcBar_arcBackColor, 0x888888)
        frontColor = typedArray.getColor(R.styleable.ArcBar_arcFrontColor, 0x8686ff)
        text = typedArray.getString(R.styleable.ArcBar_arcText) ?: "Text"
        range = typedArray.getFloat(R.styleable.ArcBar_arcRange, 220F)
        stroke = typedArray.getDimensionPixelSize(R.styleable.ArcBar_arcStroke, dp2px(7F))
        typedArray.recycle()
        updateOval()
        refreshTheLayout()
        canvasPaint.style = Paint.Style.STROKE
        canvasPaint.strokeCap = Paint.Cap.ROUND
    }

}