package xin.z7workbench.zeropercent

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos

class ArcPercentBar(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var percent = 0F
    var range = 220F
    var text = "Text"
    var backColor = 0x888888
    var frontColor = 0x8686ff
    var orientation = 0
    var strockType = 0
        set(value) {
            field = value
            refreshTheLayout()
        }
    var strokeWidth = 1
        set(value) {
            field = dp2px(value.toFloat())
            canvasPaint.strokeWidth = strokeWidth.toFloat()
            refreshTheLayout()
        }
    private val tag = "arc"
    private lateinit var oval: RectF
    private val canvasPaint = Paint()
    private val textPaint = Paint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val angle = when (orientation) {
            0 -> 270F
            1 -> 90F
            2 -> 180F
            else -> 90F
        }
        canvasPaint.color = backColor
        canvasPaint.strokeWidth = if (strockType == 1)
            strokeWidth.toFloat() * goldenSection()
        else
            strokeWidth.toFloat()
        canvas.drawArc(oval, angle - range / 2, range, false, canvasPaint)
        canvasPaint.color = frontColor
        canvasPaint.strokeWidth = strokeWidth.toFloat()
        if (angle.toInt() % 180 == 0)
            canvas.drawArc(oval, angle - range / 2, percent * range / 100, false, canvasPaint)
        else if (angle == 180F)
            canvas.drawArc(oval, angle - range / 2 - percent * range / 100, percent * range / 100, false, canvasPaint)
        else canvas.drawArc(oval, angle - range / 2 + percent * range / 100, percent * range / 100, false, canvasPaint)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateOval()
    }

    private fun refreshTheLayout() {
        invalidate()
        requestLayout()
    }

    private fun widthHeightRatio(): Float {
        val xp = paddingLeft + paddingRight
        val x = width - xp
        val angle = range / 2

        return if (angle < 90 && angle <= 180) x.toFloat() / (x * (cos(180 - angle) + 1) / 2)
        else x.toFloat() / (x / 2).toFloat()
    }

    private fun updateOval() {
        val xp = paddingLeft + paddingRight
        val yp = paddingBottom + paddingTop
        oval = if (width - xp < height - yp)
            RectF((paddingLeft + strokeWidth).toFloat(), (paddingTop + strokeWidth).toFloat(),
                    (paddingLeft + (width - xp) - strokeWidth).toFloat(),
                    (paddingTop + (width - xp) - strokeWidth).toFloat())
        else RectF((paddingLeft + strokeWidth).toFloat(), (paddingTop + strokeWidth).toFloat(),
                (paddingLeft + (height - xp) - strokeWidth).toFloat(),
                (paddingTop + (height - yp) - strokeWidth).toFloat())
    }

    private fun dp2px(value: Float) = (_context.resources.displayMetrics.density * value + 0.5f).toInt()

    init {
        val array = _context.theme.obtainStyledAttributes(attrs,
                R.styleable.ArcPercentBar, 0, 0)
        percent = array.getFloat(R.styleable.ArcPercentBar_arcPercent, 0F)
        backColor = array.getColor(R.styleable.ArcPercentBar_arcBackColor, 0x888888)
        frontColor = array.getColor(R.styleable.ArcPercentBar_arcFrontColor, 0x8686ff)
        text = array.getString(R.styleable.ArcPercentBar_arcText) ?: "Text"
        range = array.getFloat(R.styleable.ArcPercentBar_arcRange, 220F)
        strokeWidth = array.getDimensionPixelSize(R.styleable.ArcPercentBar_arcStrokeWidth, dp2px(7F))
        strockType = array.getInteger(R.styleable.ArcPercentBar_arcBackStrokeType, 0)
        orientation = array.getInteger(R.styleable.ArcPercentBar_arcOrientation, 0)
        array.recycle()
        updateOval()
        refreshTheLayout()
        canvasPaint.style = Paint.Style.STROKE
        canvasPaint.strokeCap = Paint.Cap.ROUND
    }

}