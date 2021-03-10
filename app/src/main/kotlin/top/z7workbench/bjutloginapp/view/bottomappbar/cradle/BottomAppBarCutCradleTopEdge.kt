package top.z7workbench.bjutloginapp.view.bottomappbar.cradle

import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment
import com.google.android.material.shape.ShapePath

class BottomAppBarCutCradleTopEdge(
        private val fabMargin: Float,
        roundedCornerRadius: Float,
        private val cradleVerticalOffset: Float
) : BottomAppBarTopEdgeTreatment(fabMargin, roundedCornerRadius, cradleVerticalOffset) {

    @SuppressWarnings("RestrictTo")
    override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
        val fabDiameter = fabDiameter
        if (fabDiameter == 0f) {
            shapePath.lineTo(length, 0F)
            return
        }

        val diamondSize = fabDiameter / 2F
        val middle = center + horizontalOffset

        val verticalOffsetRatio = cradleVerticalOffset / diamondSize
        if (verticalOffsetRatio >= 1.0F) {
            shapePath.lineTo(length, 0F)
            return
        }

        shapePath.lineTo(middle - (fabMargin + diamondSize - cradleVerticalOffset), 0F)
        shapePath.lineTo(middle, (diamondSize - cradleVerticalOffset + fabMargin) * interpolation)
        shapePath.lineTo(middle + (fabMargin + diamondSize - cradleVerticalOffset), 0F)
        shapePath.lineTo(length, 0F)
    }
}