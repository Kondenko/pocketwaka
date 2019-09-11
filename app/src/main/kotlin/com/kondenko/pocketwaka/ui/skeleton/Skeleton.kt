package com.kondenko.pocketwaka.ui.skeleton

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.extensions.adjustForDensity
import com.kondenko.pocketwaka.utils.extensions.findViewsWithTag
import kotlin.math.roundToInt

class Skeleton(
        private val context: Context,
        private val root: View? = null,
        var skeletonBackground: Drawable =
                context.getDrawable(R.drawable.all_skeleton_text) ?: ColorDrawable(Color.TRANSPARENT),
        var skeletonHeight: Int =
                context.resources.getDimension(R.dimen.height_all_skeleton_text).roundToInt(),
        var skeletonStateChanged: ((View, Boolean) -> Unit)? = null
) {

    private data class InitialState(
            val text: CharSequence?,
            val backgroundDrawable: Drawable?,
            val imageSource: Drawable?,
            val width: Int
    )

    var animDuration: Long = 300

    var animateChanges: Boolean = false

    private val pulseAnimations: MutableSet<ValueAnimator> = mutableSetOf()

    private val initialStates: MutableMap<View, InitialState> by lazy { findViews(root) }

    var isShown: Boolean = false
        private set

    fun addViews(root: ViewGroup) {
        findViews(root).forEach { (view, state) ->
            if (!initialStates.containsKey(view)) {
                initialStates[view] = state
            }
        }
    }

    fun addView(view: View) {
        if (view.getTag(R.id.tag_skeleton_width_key) != null) {
            initialStates[view] = view.getInitialState()
        }
    }

    private fun findViews(root: View?) =
            root?.findViewsWithTag(R.id.tag_skeleton_width_key, null)
                    ?.associateWith { it.getInitialState() }
                    ?.toMutableMap()
                    ?: mutableMapOf()

    private fun View.getInitialState() =
            InitialState(
                    text = (this as? TextView)?.text,
                    backgroundDrawable = this.background,
                    imageSource = (this as? ImageView)?.drawable,
                    width = width
            )

    fun show() {
        initialStates.keys
                .forEach {
                    it.showSkeleton()
                    it.playPulseAnimation()
                }
        isShown = true
    }

    fun hide() {
        initialStates.keys.forEach { it.hideSkeleton() }
        stopPulseAnimations()
        isShown = false
    }

    private fun View.showSkeleton() {
        val dimenWidth = (getTag(R.id.tag_skeleton_width_key) as String?)?.toInt()
        val finalWidth = this@Skeleton.context.adjustForDensity(dimenWidth)?.roundToInt().let {
            if (it == null || it < 0) width else it
        }
        animateIn {
            this.updateLayoutParams {
                width = finalWidth
            }
            if (isShown) skeletonStateChanged?.invoke(this, true)
            val verticalInset = -((height - skeletonHeight) / 2)
            background = InsetDrawable(skeletonBackground, 0, verticalInset, 0, verticalInset)
            (this as? TextView)?.text = null
            (this as? ImageView)?.setImageBitmap(null)
        }
    }

    private fun View.hideSkeleton() = initialStates[this]?.let {
        animateIn {
            updateLayoutParams {
                this.width = it.width
            }
            if (!isShown) skeletonStateChanged?.invoke(this, false)
            background = it.backgroundDrawable
            (this as? TextView)?.text = it.text
            (this as? ImageView)?.setImageDrawable(it.imageSource)
        }
    }

    private fun View.animateIn(updateView: () -> Unit) {
        if (animateChanges) {
            alpha = 0f
            animate()
                    .withStartAction(updateView)
                    .alpha(1f)
                    .setDuration(if (!isShown) animDuration / 2 else animDuration)
                    .start()
        } else {
            updateView()
        }
    }

    private fun View.playPulseAnimation() {
        val alphaDimmed = 0.59f
        val alphaFull = 1f
        pulseAnimations += ValueAnimator.ofFloat(alphaDimmed, alphaFull).apply {
            duration = 650L
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener { this@playPulseAnimation.alpha = it.animatedValue as Float }
            start()
        }
    }

    private fun stopPulseAnimations() = pulseAnimations.forEach(ValueAnimator::cancel)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Skeleton
        if (root != other.root) return false
        return true
    }

    override fun hashCode(): Int {
        return root?.hashCode() ?: 0
    }


}
