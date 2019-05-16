package com.kondenko.pocketwaka.ui

import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.view.updateLayoutParams
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import kotlin.math.roundToInt

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
        private val transform: ((View, Boolean) -> Unit)? = null
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
        val dimenWidth = (getTag(R.id.tag_skeleton_width_key) as String).toInt()
        this.updateLayoutParams {
            width = skeletonWidth ?: context.adjustForDensity(dimenWidth).roundToInt()
            height = skeletonHeight ?: height
        }
        this.background = skeletonBackground
        transform?.invoke(this, true)
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        updateLayoutParams {
            this.width = it.width
            this.height = it.height
        }
        this.background = it.backgroundDrawable
        this.alpha = it.alpha
        transform?.invoke(this, false)
    }

}