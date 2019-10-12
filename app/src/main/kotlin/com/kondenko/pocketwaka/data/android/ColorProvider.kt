package com.kondenko.pocketwaka.data.android

import android.content.Context
import android.graphics.Color
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.stats.model.StatsItem
import java.util.*


class ColorProvider(val context: Context) {

    private val saturation = 0.65f

    private val value = 1f

    fun provideColors(items: List<StatsItem>?): List<StatsItem>? {
        if (items == null) return null
        val predefinedColors = context.resources.getIntArray(R.array.chart_colors)
        return items.mapIndexed { i, item ->
            val color =
                  predefinedColors.getOrNull(i) ?: getRandomColor(item.name.hashCode().toLong())
            item.copy(color = color)
        }
    }

    private fun getRandomColor(seed: Long): Int = getColorByHue(Random(seed).nextInt(360).toFloat())

    private fun getColorByHue(hue: Float) = Color.HSVToColor(floatArrayOf(hue, saturation, value))

}