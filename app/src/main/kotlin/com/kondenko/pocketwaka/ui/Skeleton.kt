package com.kondenko.pocketwaka.ui

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.findViewsWithTag
import kotlin.math.roundToInt

private data class InitialState(
        val text: CharSequence?,
        val width: Int,
        val height: Int,
        val backgroundDrawable: Drawable?
)

fun View.isSkeleton() = getTag(R.id.tag_skeleton_width_key) != null

class Skeleton(
        private val root: ViewGroup,
        private val skeletonBackground: Drawable,
        private val skeletonHeight: Int? = null,
        private val transform: ((View, Boolean) -> Unit)? = null
) {

    private var initialStates: Map<View, InitialState> = emptyMap()

    fun refreshViews() {
        initialStates = root.findViewsWithTag(R.id.tag_skeleton_width_key, null).associateWith {
            InitialState((it as? TextView)?.text, it.width, it.height, it.background)
        }
    }

    fun show() = initialStates.keys.forEach { it.showSkeleton() }

    fun hide() = initialStates.keys.forEach { it.hideSkeleton() }

    private fun View.showSkeleton() {
        val dimenWidth = (getTag(R.id.tag_skeleton_width_key) as String?)?.toInt()
        val finalWidth = context.adjustForDensity(dimenWidth)?.roundToInt().let {
            if (it == null || it < 0) width else it
        }
        this.updateLayoutParams {
            width = finalWidth
            height = skeletonHeight ?: height
        }
        transform?.invoke(this, true)
        background = skeletonBackground
        (this as? TextView)?.text = null
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        updateLayoutParams {
            this.width = it.width
            this.height = it.height
        }
        transform?.invoke(this, false)
        background = it.backgroundDrawable
        (this as? TextView)?.text = it.text
    }

}
