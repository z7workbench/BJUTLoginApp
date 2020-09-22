package xin.z7workbench.zeropercent

import android.content.Context
import kotlin.math.sqrt

fun goldenSection() = (sqrt(5F) - 1) / 2

fun Context.dp2px(value: Float) = (resources.displayMetrics.density * value + 0.5F).toInt()

fun Context.sp2px(value: Float) = (resources.displayMetrics.scaledDensity * value - 36F).toInt()