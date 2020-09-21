package xin.z7workbench.zeropercent

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class ArcPercentBar(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var percent = 0F
        set(value) {
            field = value.coerceAtLeast(0F).coerceAtMost(100F)
            if (autoText) text = "${percent.toBigDecimal().setScale(2)}%"
        }
    var range = 220F
    var text = "Text"
    var backColor = 0x888888
    var frontColor = 0x8686ff
    var orientation = 0
    var autoText = true
    var textSize = _context.sp2px(10F)
        set(value) {
            field = _context.sp2px(value.toFloat())
            textPaint.textSize = textSize.toFloat()
            refresh()
        }
    var textColor = 0xCDCDCD
    var strokeType = 0
        set(value) {
            field = value
            refresh()
        }
    var strokeWidth = _context.dp2px(7F)
        set(value) {
            field = _context.dp2px(value.toFloat())
            canvasPaint.strokeWidth = strokeWidth.toFloat()
            refresh()
        }
    private val tag = "arc"
    private lateinit var round: RectF
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
        canvasPaint.strokeWidth = when (strokeType) {
            1 -> strokeWidth.toFloat() * goldenSection()
            2 -> strokeWidth.toFloat() / goldenSection()
            else -> strokeWidth.toFloat()
        }
        canvas.drawArc(round, angle - range / 2, range, false, canvasPaint)
        canvasPaint.color = frontColor
        canvasPaint.strokeWidth = strokeWidth.toFloat()
        when {
            angle.toInt() % 180 == 0 -> canvas.drawArc(round, angle - range / 2, percent * range / 100, false, canvasPaint)
            angle == 180F -> canvas.drawArc(round, angle - range / 2 - percent * range / 100, percent * range / 100, false, canvasPaint)
            else -> canvas.drawArc(round, angle - range / 2 + percent * range / 100, percent * range / 100, false, canvasPaint)
        }

        if (text.isNotEmpty()) {
            val top = textPaint.measureText(text)
            textPaint.color = textColor
            canvas.drawText(text, (width - top) / 2,
                    height / 2 - ((textPaint.descent() + textPaint.ascent()) / 2), textPaint);
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateRound()
    }

    private fun refresh() {
        invalidate()
        requestLayout()
    }

    private fun updateRound() {
        val xp = paddingLeft + paddingRight
        val yp = paddingBottom + paddingTop
        round = if (width - xp < height - yp)
            RectF((paddingLeft + strokeWidth).toFloat(), (paddingTop + strokeWidth).toFloat(),
                    (paddingLeft + (width - xp) - strokeWidth).toFloat(),
                    (paddingTop + (width - xp) - strokeWidth).toFloat())
        else RectF((paddingLeft + strokeWidth).toFloat(), (paddingTop + strokeWidth).toFloat(),
                (paddingLeft + (height - xp) - strokeWidth).toFloat(),
                (paddingTop + (height - yp) - strokeWidth).toFloat())
    }

    init {
        val array = _context.theme.obtainStyledAttributes(attrs,
                R.styleable.ArcPercentBar, 0, 0)
        autoText = array.getBoolean(R.styleable.ArcPercentBar_arcAutoText, true)
        percent = array.getFloat(R.styleable.ArcPercentBar_arcPercent, 0F)
        backColor = array.getColor(R.styleable.ArcPercentBar_arcBackColor, 0x888888)
        frontColor = array.getColor(R.styleable.ArcPercentBar_arcFrontColor, 0x8686ff)
        text = if (!autoText) array.getString(R.styleable.ArcPercentBar_arcText)
                ?: "${percent.toBigDecimal().setScale(2)}%"
        else "${percent.toBigDecimal().setScale(2)}%"
        textSize = array.getDimensionPixelSize(R.styleable.ArcPercentBar_arcTextSize, _context.sp2px(10F))
        textColor = array.getColor(R.styleable.ArcPercentBar_arcTextColor, 0xCDCDCD)
        range = array.getFloat(R.styleable.ArcPercentBar_arcRange, 220F)
        strokeWidth = array.getDimensionPixelSize(R.styleable.ArcPercentBar_arcStrokeWidth, _context.dp2px(7F))
        strokeType = array.getInteger(R.styleable.ArcPercentBar_arcBackStrokeType, 0)
        orientation = array.getInteger(R.styleable.ArcPercentBar_arcOrientation, 0)

        array.recycle()
        updateRound()
        canvasPaint.style = Paint.Style.STROKE
        canvasPaint.strokeCap = Paint.Cap.ROUND
        textPaint.style = Paint.Style.FILL
        refresh()
    }
}