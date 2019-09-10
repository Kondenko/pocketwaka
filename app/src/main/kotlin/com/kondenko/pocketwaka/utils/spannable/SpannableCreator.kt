package com.kondenko.pocketwaka.utils.spannable

import android.text.Spannable

interface SpannableCreator {

    fun create(string: String?): Spannable?

}
