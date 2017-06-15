package com.kondenko.pocketwaka.utils

import android.content.Context
import android.graphics.Color
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.api.model.stats.StatsItem
import java.util.*


object ColorGenerator {

    private val TAG = "ColorGenerator"

    private val SATURATION = 0.65f
    private val VALUE = 1f

    fun getColors(context: Context, items: List<StatsItem>): ArrayList<Int> {
        val n = items.size
        val predefinedColors = getPredefinedColors(context)
        val colors = ArrayList<Int>(n)
        for (i in 0..(n - 1)) {
            val color: Int
            if (i <= predefinedColors.size - 1) {
                color = predefinedColors[i]
            } else {
                color = getRandomColor(items[i].name.hashCode().toLong())
            }
            colors.add(color)
        }
        return colors
    }

    private fun getPredefinedColors(context: Context): IntArray = context.resources.getIntArray(R.array.chart_colors)

    private fun getRandomColor(seed: Long): Int {
        val random = Random(seed)
        return getColorByHue(random.nextInt(360).toFloat())
    }

    private fun getColorByHue(hue: Float) = Color.HSVToColor(floatArrayOf(hue, SATURATION, VALUE))

}