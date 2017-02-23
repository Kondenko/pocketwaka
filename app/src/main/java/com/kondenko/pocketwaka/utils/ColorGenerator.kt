package com.kondenko.pocketwaka.utils

import android.graphics.Color
import java.util.*


object ColorGenerator {

    private val SATURATION = 0.65f
    private val VALUE = 1f

    fun getColors(n: Int): ArrayList<Int> {
        val random = Random()
        val colors = ArrayList<Int>(n)
        val part: Float = 360 / n.toFloat()
        for (i in 0..(n - 1)) {
            // Shift a value left or right from the current position in range of the current sector
            val randomFactor = random.nextInt(Math.round(part / 2)) * if (random.nextBoolean()) 1 else -1
            val hue: Float = (360 - part * i) + randomFactor
            val hueOpposite: Float = (hue - 180) + randomFactor
            val color = getColorByHue(hue)
            val colorOpposite = getColorByHue(hueOpposite)
            colors.add(color)
            colors.add(colorOpposite)
        }
        return colors
    }

    private fun getColorByHue(hue: Float) = Color.HSVToColor(floatArrayOf(hue, SATURATION, VALUE))

}