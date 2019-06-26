package com.kondenko.pocketwaka.utils.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleableRes
import androidx.core.view.children
import com.jakewharton.rxbinding2.view.RxView

fun View.useAttributes(attrs: AttributeSet?, @StyleableRes styleable: IntArray, defStyleAttr: Int = 0, defStyleRes: Int = 0, actions: TypedArray.() -> Unit) {
    attrs?.let {
        with(context.obtainStyledAttributes(it, styleable, defStyleAttr, defStyleRes)) {
            actions()
            recycle()
        }
    }
}

fun <T> ViewGroup.findViewsWithTag(id: Int, value: T? = null): List<View> {
    val childrenWithTag = mutableListOf<View>()
    children.forEach {
        if (it is ViewGroup) {
            childrenWithTag += it.findViewsWithTag(id, value)
        }
        val tag = it.getTag(id)
        val areValuesEqual = value != null && tag == value
        if (tag != null || areValuesEqual) childrenWithTag += it
    }
    return childrenWithTag
}

fun View.rxClicks() = RxView.clicks(this)

fun View.setInvisible() {
    visibility = View.INVISIBLE
}