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
import io.reactivex.disposables.Disposable

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

fun <T> ViewGroup.findViewsWithTag(id: Int, value: T? = null): List<View> {
    val childrenWithTag = mutableListOf<View>()
    children.forEach {
        if (it is ViewGroup) {
            childrenWithTag += it.findViewsWithTag(id, value)
        }
        // Because getTag() returns String, that's why️
        val tag = it.getTag(id)
        val areValuesEqual = value != null && tag == value
        if (tag != null || areValuesEqual) childrenWithTag += it
    }
    return childrenWithTag
}

fun View.rxClicks() = RxView.clicks(this)

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

fun post(vararg views: View, action: () -> Unit): Disposable {
    return post(*views).subscribe(action)
}

fun View.post(): Completable {
    return Completable.create {
        post {
            it.onComplete()
        }
    }
}