package com.kondenko.pocketwaka.domain.menu

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.core.view.doOnLayout
import androidx.core.view.isGone
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding3.view.clicks
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.ui.Scale
import com.kondenko.pocketwaka.ui.scale
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.dp
import com.kondenko.pocketwaka.utils.extensions.setMargins
import com.kondenko.pocketwaka.utils.extensions.setSize
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.dialog_app_rating.*
import kotlinx.android.synthetic.main.dialog_app_rating.view.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class AppRatingBottomSheetDialog : BottomSheetDialogFragment() {

    private val ratingChanges = PublishSubject.create<Int>()

    private val sendFeedbackClicks = PublishSubject.create<Unit>()

    private var isNegativeFeedbackStateShown = false

    var ratingReactionDelay: Long = 300

    var expandedHeightDp = 246

    var ratingBarScaleCollapsed = 0.7f

    var rating: Int = 0

    var onDismiss: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_app_rating, container, false)
    }

    @SuppressLint("CheckResult")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ratingbar_rating_dialog.ratingChanges().subscribeWith(ratingChanges)
        button_low_rating_action.clicks().subscribeWith(sendFeedbackClicks)
        ratingbar_rating_dialog.rating = rating
    }

    override fun onDestroyView() {
        onDismiss = null
        super.onDestroyView()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        WakaLog.d("onDismiss = $onDismiss")
        onDismiss?.invoke()
    }

    fun ratingChanges(): Observable<Int> =
          ratingChanges.delay(ratingReactionDelay, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())

    fun sendFeedbackClicks() =
          sendFeedbackClicks

    fun showLowRatingState(show: Boolean, isMailAvailable: Boolean, supportEmail: String?) {
        if (isNegativeFeedbackStateShown && !show || !isNegativeFeedbackStateShown && show) {
            isNegativeFeedbackStateShown = show
            if (!isMailAvailable && supportEmail != null) {
                textview_low_rating_message.text = getString(R.string.rating_dialog_low_rating_message_no_email_app, supportEmail)
            } else if (!isMailAvailable && supportEmail == null) {
                textview_low_rating_message.text = getString(R.string.rating_dialog_low_rating_message_no_email_address)
            }
            val parent = view as? ViewGroup
            parent?.doOnLayout {
                parent.updateTitleMargin(show)
                parent.animateLayout(!show, !show || !isMailAvailable && supportEmail == null)
            }
        }
    }

    private fun View.updateTitleMargin(showLowRatingState: Boolean) {
        val ratingBarMarginBottom = context
              .resources
              .getDimension(R.dimen.margin_rating_dialog_rating_bar_bottom).roundToInt()
        ratingbar_rating_dialog.setMargins(bottom = if (showLowRatingState) 0 else ratingBarMarginBottom)
    }

    private fun ViewGroup.animateLayout(showLowRatingMesssage: Boolean, showLowRatingAction: Boolean) {
        val initialDialogHeight = height.toFloat()
        val targetDialogHeight = requireContext().dp(expandedHeightDp)
        val initialRatingBarScale = 1f
        val targetRatingBarScale = Scale.of(ratingBarScaleCollapsed)
        ValueAnimator.ofFloat(0f, 1f).apply {
            interpolator = DecelerateInterpolator()
            duration = 300
            addUpdateListener {
                val fraction = animatedValue as Float
                val sheetHeight = fraction.fractionToValue(initialDialogHeight, targetDialogHeight)
                val ratingBarScale = fraction.fractionToValue(initialRatingBarScale, targetRatingBarScale.value!!)
                setSize(height = sheetHeight.roundToInt())
                ratingbar_rating_dialog.scale = Scale.of(ratingBarScale)
            }
            textview_low_rating_message?.isGone = showLowRatingMesssage
            button_low_rating_action?.isGone = showLowRatingAction
            TransitionManager.beginDelayedTransition(this@animateLayout)
            start()
        }
    }

    private fun Float.fractionToValue(initialValue: Float, targetValue: Float) =
          initialValue + (this * (targetValue - initialValue))

}