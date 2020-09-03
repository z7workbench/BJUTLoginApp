package xin.z7workbench.zeropercent

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View

class WavePercentView(private val _context: Context, attrs: AttributeSet) : View(_context, attrs) {
    var commonColor = 0x46C59C
    var warningColor = 0xFECD00
    var percent = 75F

    private fun getLevel() = when {
        (percent <= 0F) -> Level.COMMON to 0F
        (percent > 0F && percent <= 100F) -> Level.COMMON to percent
        (percent > 100F && percent <= 200F) -> Level.WARNING to percent - 100
        else -> Level.WARNING to 100F
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    init {
        val array = _context.theme.obtainStyledAttributes(attrs, R.styleable.WavePercentView,
                0, 0)
        commonColor = array.getColor(R.styleable.WavePercentView_waveCommonColor, 0x46C59C)
        warningColor = array.getColor(R.styleable.WavePercentView_waveWarningColor, 0x46C59C)
        percent = array.getFloat(R.styleable.WavePercentView_wavePercent, 75F)
    }

    internal enum class Level {
        COMMON, WARNING
    }
}