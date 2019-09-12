package com.kondenko.pocketwaka.utils.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleableRes
import androidx.core.view.children
import androidx.core.view.updateLayoutParams

fun View.useAttributes(attrs: AttributeSet?, @StyleableRes styleable: IntArray, defStyleAttr: Int = 0, defStyleRes: Int = 0, actions: TypedArray.() -> Unit) {
    attrs?.let {
        with(context.obtainStyledAttributes(it, styleable, defStyleAttr, defStyleRes)) {
            actions()
            recycle()
        }
    }
}

fun <T> View.findViewsWithTag(id: Int, value: T? = null): List<View> {
    val childrenWithTag = mutableListOf<View>()

    fun addIfHasTag(view: View) {
        val tag = view.getTag(id)
        val areValuesEqual = value != null && tag == value
        if (tag != null || areValuesEqual) childrenWithTag += view
    }

    if (this is ViewGroup) {
        children.forEach {
            if (it is ViewGroup) {
                childrenWithTag += it.findViewsWithTag(id, value)
            }
            addIfHasTag(it)
        }
    } else {
        addIfHasTag(this)
    }

    return childrenWithTag
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun setViewsVisibility(visibility: Int, vararg views: View) = views.forEach { it.visibility = visibility }

fun setGone(gone: Boolean, vararg views: View) = setViewsVisibility(View.GONE, *views)

var View.currentHeight: Int
    get() = height
    set(value) {
        updateLayoutParams {
            height = value
        }
    }