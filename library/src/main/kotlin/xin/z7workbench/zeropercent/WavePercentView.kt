package xin.z7workbench.zeropercent

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.getResourceIdOrThrow
import java.lang.IllegalArgumentException

class WavePercentView(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var amplitude = 0.1F
    var backColor = 0x000000
    var levelColors = intArrayOf(0x46C59C, 0xFECD00)
    var percent = 75F
    

    private fun getLevel() = when {
        (percent.toInt() / 100 < 0) -> 0 to 0F
        (percent.toInt() / 100 >= levelColors.size) -> levelColors.size - 1 to 100F
        else -> percent.toInt() / 100 to percent % 100
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

    }

    init {
        val array = _context.theme.obtainStyledAttributes(attrs, R.styleable.WavePercentView,
                0, 0)
        amplitude = array.getFloat(R.styleable.WavePercentView_waveAmplitude, 50F)
        backColor = array.getColor(R.styleable.WavePercentView_waveBackColor, 0x000000)
        percent = array.getFloat(R.styleable.WavePercentView_wavePercent, 75F)
        levelColors = try {
            val id = array.getResourceIdOrThrow(R.styleable.WavePercentView_waveLevelColors)
            if (_context.resources.getIntArray(id).isEmpty()) {
                intArrayOf(0x46C59C, 0xFECD00)
            } else _context.resources.getIntArray(id)
        } catch (e: Exception) {
            intArrayOf(0x46C59C, 0xFECD00)
        }
    }
}