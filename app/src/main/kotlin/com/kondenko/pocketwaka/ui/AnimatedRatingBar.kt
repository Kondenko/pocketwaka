package com.kondenko.pocketwaka.ui

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import androidx.customview.view.AbsSavedState
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
      defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private class SavedState(val currentRating: Int, superState: Parcelable) : AbsSavedState(superState)

    private val ratingChanges = PublishSubject.create<Int>()

    private val indices = mutableMapOf<Int, Int>() // id to index

    private var animationDuration: Int = 150

    var starsNumber: Int = 5

    var rating = 0
        set(value) {
            require(value in 1..starsNumber)
            field = value
            updateChildrenState(value - 1)
        }

    init {
        useAttributes(attrs, R.styleable.AnimatedRatingBar, defStyleAttr) {
            starsNumber = getInteger(R.styleable.AnimatedRatingBar_stars_number, 5)
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
                          .doOnNext { rating = it }
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

    override fun onSaveInstanceState(): Parcelable? {
        return super.onSaveInstanceState()?.let { SavedState(rating, it) }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state !is SavedState) {
            super.onRestoreInstanceState(state)
        } else {
            super.onRestoreInstanceState(state.superState)
            rating = state.currentRating
        }
    }
}