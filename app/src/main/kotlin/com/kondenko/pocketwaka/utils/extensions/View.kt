package com.kondenko.pocketwaka.utils.extensions

import android.content.res.TypedArray
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StyleableRes
import androidx.core.view.ViewCompat
import androidx.core.view.children
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Completable

fun View.elevation(elevation: Float) {
    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP) ViewCompat.setElevation(this, elevation)
    else this.elevation = elevation
}

fun View.useAttributes(attrs: AttributeSet?, @StyleableRes styleable: IntArray, defStyleAttr: Int = 0, defStyleRes: Int = 0, actions: TypedArray.() -> Unit) {
    attrs?.let {
        with(context.obtainStyledAttributes(it, styleable, defStyleAttr, defStyleRes)) {
            actions()
            recycle()
        }
    }
}

fun <T> ViewGroup.findViewsWithTag(id: Int, value: T): List<View> {
    val childrenWithTag = mutableListOf<View>()
    children.forEach {
        if (it is ViewGroup) childrenWithTag += it.findViewsWithTag(id, value)
        // Because getTag() returns String, that's why️
        val areValuesEqual = it.getTag(id)?.toString() == value.toString()
        if (areValuesEqual) childrenWithTag.add(it)
    }
    return childrenWithTag
}

fun View.rxClicks() = RxView.clicks(this)

fun showFirstView(visibleView: View, vararg goneViews: View) {
    visibleView.setVisible()
    goneViews.forEach { it.setGone() }
}

fun View.setVisible() {
    visibility = View.VISIBLE
}

fun View.setInvisible() {
    visibility = View.INVISIBLE
}

fun View.setGone() {
    visibility = View.GONE
}

fun post(vararg views: View) = Completable.merge(views.map(View::post))

fun View.post(): Completable {
    return Completable.create {
        post {
            it.onComplete()
        }
    }
}

fun View.post(action: (View) -> Unit) {
    post {
        action(this)
    }
}