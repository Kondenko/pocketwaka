package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.graphics.Color
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import java.util.*


class ColorProvider(val context: Context) {

    private val saturation = 0.65f

    private val value = 1f

    fun provideColors(items: List<StatsItem>): ArrayList<Int> {
        val n = items.size
        val predefinedColors = getPredefinedColors()
        val colors = ArrayList<Int>(n)
        items.forEachIndexed { i, item ->
            val color: Int = if (i <= predefinedColors.size - 1) {
                predefinedColors[i]
            } else {
                getRandomColor(item.name.hashCode().toLong())
            }
            colors.add(color)
        }
        return colors
    }

    private fun getPredefinedColors(): IntArray = context.resources.getIntArray(R.array.chart_colors)

    private fun getRandomColor(seed: Long): Int = getColorByHue(Random(seed).nextInt(360).toFloat())

    private fun getColorByHue(hue: Float) = Color.HSVToColor(floatArrayOf(hue, saturation, value))

}