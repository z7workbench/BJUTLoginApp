package top.z7workbench.zeropercent

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Shader
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getResourceIdOrThrow

class WavePercentView(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var amplitude = 0.1F
        set(value) {
            field = value.coerceAtMost(50F)
        }
    var backColor = 0x000000
    var levelColors = intArrayOf(0x46C59C, 0xFECD00)
    var percent = 75F
    var strokeWidth = _context.dp2px(3F)
        set(value) {
            field = _context.dp2px(value.toFloat())
        }
    private val wavePaint = Paint()
    private val backPaint = Paint()
    private val strokePaint = Paint()
    private var canvasSize = 0
    private val matrix2 = Matrix()
    private lateinit var shader: Shader
    private val reversePercent: Float
        get() = (1 - percent) / 100
    private fun getLevel() = when {
        (percent.toInt() / 100 < 0) -> 0 to 0F
        (percent.toInt() / 100 >= levelColors.size) -> levelColors.size - 1 to 100F
        else -> percent.toInt() / 100 to percent % 100
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvasSize = width;
        if (height < canvasSize) {
            canvasSize = height;
        }
        update()
        wavePaint.shader = shader
        matrix2.setScale(1F, amplitude / 50F, 0F, measuredHeight.toFloat())
        matrix2.postTranslate(0.5F * width, reversePercent * height)
        shader.setLocalMatrix(matrix2)

        // shape: circle
        if (strokeWidth > 0) {
            canvas.drawCircle(width / 2F, height / 2F, (width - strokeWidth) / 2F - 1F, strokePaint)
        }
        val radius = width / 2F - strokeWidth
        canvas.drawCircle(width / 2F, height / 2F, radius, backPaint)
        canvas.drawCircle(width / 2F, height / 2F, radius, wavePaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        canvasSize = w
        if (h < canvasSize)
            canvasSize = h
        update()
    }

    private fun update() {
        TODO()
    }

    fun setPaintStyle(paint: Paint) {

    }

    init {
        val array = _context.theme.obtainStyledAttributes(attrs, R.styleable.WavePercentView,
                0, 0)
        amplitude = array.getFloat(R.styleable.WavePercentView_waveAmplitude, 50F)
        backColor = array.getColor(R.styleable.WavePercentView_waveBackColor, 0x000000)
        percent = array.getFloat(R.styleable.WavePercentView_wavePercent, 75F)
        strokeWidth = array.getDimensionPixelSize(R.styleable.WavePercentView_waveStrokeWidth, _context.dp2px(3F))
        levelColors = try {
            val id = array.getResourceIdOrThrow(R.styleable.WavePercentView_waveLevelColors)
            if (_context.resources.getIntArray(id).isEmpty()) {
                intArrayOf(0x46C59C, 0xFECD00)
            } else _context.resources.getIntArray(id)
        } catch (e: Exception) {
            intArrayOf(0x46C59C, 0xFECD00)
        }
        array.recycle()
    }
}