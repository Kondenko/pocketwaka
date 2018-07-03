package com.kondenko.pocketwaka.ui.onelinesegmentedchart

import android.support.annotation.ColorInt
import android.support.annotation.FloatRange

/**
 * Represents a data element that is placed in [OneLineSegmentedChart]
 *
 * All segments in a [OneLineSegmentedChart] should have the percents of such values
 * that make the sum of 100 (because we have different shares of something whole).
 *
 * @param percent value in percents to determine the bar's share in the chart view
 * @param color the color of the bar
 * @param title the title of the data element (optional)
 *
 * @sample Segment(percent = 80, color = Color.GREEN, title = "Android")
 * @sample Segment(percent = 15, color = Color.RED, title = "iOS")
 * @sample Segment(percent = 5, color = Color.BLUE, title = "Other")
 */
data class Segment(@FloatRange(from = 0.0, to = 100.0) val percent: Float, @ColorInt val color: Int, val title: String?)