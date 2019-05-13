package com.kondenko.pocketwaka.ui

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.updateLayoutParams

private data class InitialState(
        val width: Int,
        val height: Int,
        val backgroundDrawable: Drawable?,
        val alpha: Float
)

class Skeleton(
        vararg views: View,
        private val skeletonBackground: Drawable,
        private val skeletonWidth: Int? = null,
        private val skeletonHeight: Int? = null,
        private val animate: ((View) -> Unit)? = null
) {

    private val initialStates = hashMapOf<View, InitialState>()

    init {
        views.forEach {
            initialStates[it] = InitialState(it.width, it.height, it.background, it.alpha)
        }
    }

    fun show() = initialStates.keys.forEach { it.showSkeleton() }

    fun hide() = initialStates.keys.forEach { it.hideSkeleton() }

    private fun View.showSkeleton() {
        this.updateLayoutParams {
            width = skeletonWidth?:width
            height = skeletonHeight?:height
        }
        this.background = skeletonBackground
        animate?.invoke(this)
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        updateLayoutParams {
            this.width = it.width
            this.height = it.height
        }
        this.background = it.backgroundDrawable
        this.alpha = it.alpha
    }

}