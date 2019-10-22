package com.kondenko.pocketwaka.ui

import android.view.View

data class Scale(val x: Float, val y: Float) {

    companion object {
        fun of(value: Float) = Scale(value, value)
    }

    val value: Float?
        get() = if (x == y) x else null

}

var View.scale: Scale
    get() = Scale(scaleX, scaleY)
    set(value) {
        scaleX = value.x
        scaleY = value.y
    }