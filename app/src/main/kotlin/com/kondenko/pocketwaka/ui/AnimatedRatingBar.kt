package com.kondenko.pocketwaka.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import android.widget.LinearLayout
import androidx.core.view.children
import com.airbnb.lottie.LottieAnimationView
import com.jakewharton.rxbinding3.view.clicks
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.*
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlin.math.roundToInt

class AnimatedRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val ratingChanges = PublishSubject.create<Int>()

    private val indices = mutableMapOf<Int, Int>() // id to index

    private var animationDuration: Int = 150

    init {
        useAttributes(attrs, R.styleable.AnimatedRatingBar, defStyleAttr, defStyleRes) {
            val starsNumber = getInteger(R.styleable.AnimatedRatingBar_stars_number, 5)
            val starSize = getDimension(R.styleable.AnimatedRatingBar_star_size, 44f).roundToInt()
            val starPadding = getDimension(R.styleable.AnimatedRatingBar_star_padding, 4f).roundToInt()
            val tint = getColor(R.styleable.AnimatedRatingBar_tint, context.getAccentColor())
            animationDuration = getInt(R.styleable.AnimatedRatingBar_anim_duration, animationDuration).also {
                require(it >= 0) { "Animation duration should be > 0 (was $it)" }
            }
            val animationJsonRes = getResourceId(R.styleable.AnimatedRatingBar_star_json, -1).also {
                require(it != -1) { "Animation resource not specified" }
            }
            (0 until starsNumber).forEach { index ->
                val animationView = LottieAnimationView(context).apply {
                    id = View.generateViewId()
                    layoutParams = LayoutParams(starSize, starSize).apply {
                        setMargins(start = starPadding, end = starPadding)
                    }
                    setAnimation(animationJsonRes)
                    setFillTint(tint)
                    setStrokeTint(tint)
                    clicks()
                        .doOnNext { updateChildrenState(index) }
                        .map { index + 1 }
                        .subscribeWith(ratingChanges)
                }
                indices[animationView.id] = index
                addView(animationView, index)
            }
        }
    }

    fun ratingChanges(): Observable<Int> = ratingChanges

    private fun updateChildrenState(index: Int) =
        children.forEachIndexed { i, view ->
            (view as? LottieAnimationView)?.isStarred = i <= index
        }

    private var LottieAnimationView.isStarred: Boolean
        get() = isSelected
        set(value) {
            if (isSelected != value) {
                WakaLog.d("$this, starred = $value")
                val interpolator: Interpolator = if (value) AccelerateInterpolator() else DecelerateInterpolator()
                playAnimation(duration, interpolator, reverse = !value)
            }
            isSelected = value
        }

}