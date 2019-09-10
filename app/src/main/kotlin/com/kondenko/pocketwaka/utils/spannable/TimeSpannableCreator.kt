package com.kondenko.pocketwaka.utils.spannable

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.component1
import com.kondenko.pocketwaka.utils.extensions.component2
import kotlin.math.roundToInt

class TimeSpannableCreator(private val context: Context) : SpannableCreator {

    override fun create(string: String?): Spannable? {
        if (string == null) return null
        val sb = SpannableStringBuilder(string)

        // Set spans for regular text
        sb.setSpan(
                AbsoluteSizeSpan(context.resources.getDimension(R.dimen.textsize_stats_info_text).roundToInt()),
                0,
                string.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        sb.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_text_black_secondary)),
                0,
                string.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )

        // Find start and end indices of numbers
        val numberRegex = "\\d+".toRegex()
        val numberIndices = numberRegex.findAll(string).map { it.range }

        // Highlight numbers with spans
        for ((from, to) in numberIndices) {
            val toActual = to + 1
            sb.setSpan(
                    AbsoluteSizeSpan(context.resources.getDimension(R.dimen.textsize_stats_info_number).roundToInt()),
                    from,
                    toActual,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            sb.setSpan(
                    ForegroundColorSpan(ContextCompat.getColor(context, R.color.color_text_black_primary)),
                    from,
                    toActual,
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
        }

        return sb
    }

}
