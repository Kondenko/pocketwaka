package com.kondenko.pocketwaka.utils.spannable

import android.text.Spannable
import androidx.annotation.DimenRes

interface SpannableCreator {

    fun create(string: String?, @DimenRes digitsSize: Int, @DimenRes textSize: Int): Spannable?

}
