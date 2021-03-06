package com.kondenko.pocketwaka.utils.extensions

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleableRes
import androidx.core.view.*
import kotlin.math.max

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

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun setViewsVisibility(visibility: Int, vararg views: View) = views.forEach { it.visibility = visibility }

fun View.setSize(width: Int = this.width, height: Int = this.height) = updateLayoutParams {
    this.width = width
    this.height = height
}

fun View.setMargins(start: Int = marginStart, top: Int = marginTop, end: Int = marginEnd, bottom: Int = marginBottom) =
    layoutParams?.run {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            this.setMargins(start, top, end, bottom)
        }
    }

fun <T : ViewGroup.MarginLayoutParams> T.setMargins(
    start: Int = marginStart,
    top: Int = topMargin,
    end: Int = marginEnd,
    bottom: Int = bottomMargin
) {
    this.marginStart = start
    this.topMargin = top
    this.marginEnd = end
    this.bottomMargin = bottom
}

/**
 * Make sure a [View] doesn't push [other] outside of the layout if it's too wide.
 */
fun View.limitWidthBy(other: View, vararg viewsToExclude: Int) = doOnPreDraw {
    val parentWidth = (it.parent as View).run { width - max(paddingLeft, paddingStart) - max(paddingRight, paddingEnd) }
    val otherViewsWidth = (it.parent as ViewGroup).getOtherViewsWidthSum(*viewsToExclude, it.id)
    if (other.right >= parentWidth) {
        it.updateLayoutParams {
            width = parentWidth - otherViewsWidth - it.run { max(marginRight, marginEnd) + max(marginStart, marginLeft) }
        }
    }
}

fun ViewGroup.getOtherViewsWidthSum(vararg viewsToExclude: Int) =
    children
        .filter { it.id !in viewsToExclude }
        .map { it.widthWithMargins }
        .sum()

val View.widthWithMargins
    get() = width + max(marginLeft, marginStart) + max(marginRight, marginEnd)

fun View.findViewWithParent(predicate: (ViewGroup) -> Boolean): View? = when {
    parent == null -> null
    predicate(parent as ViewGroup) -> this
    else -> (parent as View).findViewWithParent(predicate)
}
