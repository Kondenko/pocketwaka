package com.kondenko.pocketwaka.ui

import android.graphics.Color
import com.kondenko.pocketwaka.api.model.stats.StatsItem
import java.util.*

object ColorGenerator {

    private val COLOR_CEILING = 255

    fun getColor(objects: List<StatsItem>): Int {
        val random = Random()
        var color = getRandomColor(random)
        for (obj in objects) {
            if (obj.color == StatsItem.DEFAULT_COLOR_VAL) return color
            while (obj.color == color) {
                color = getRandomColor(random)
            }
        }
        return color
    }

    private fun getRandomColor(random: Random): Int {
        return Color.rgb(random.nextInt(COLOR_CEILING), random.nextInt(COLOR_CEILING), random.nextInt(COLOR_CEILING))
    }

}