package com.kondenko.pocketwaka

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.ui.TopSheetBehavior
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.extensions.createColorAnimator
import com.kondenko.pocketwaka.utils.extensions.findViewWithParent
import kotlinx.android.synthetic.main.fragment_date_picker.*

class FragmentDatePicker : Fragment() {

    private val surfaceColorResting = R.color.color_window_background

    private val surfaceColorElevated = R.color.color_background_white

    private val initialElevation = 6f

    private val finalElevation = 12f

    private val toolbarSlideOffsetBoundary = 0.5f

    private lateinit var surfaceColorAnimator: ValueAnimator

    private var animationRequired = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewWithParent { it is CoordinatorLayout }?.setupBottomSheetBehavior()
    }

    fun setTitle(title: String) {
        textview_summary_current_date.text = title
    }

    private fun View.setupBottomSheetBehavior() {
        val behavior = TopSheetBehavior.from(this)
        isClickable = true
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.state_list_animator_date_picker)
        surfaceColorAnimator = createColorAnimator(
              context,
              surfaceColorResting,
              surfaceColorElevated,
              context.resources.getInteger(R.integer.duration_datepicker_elevation_anim).toLong()
        ) { color ->
            activity?.window?.statusBarColor = color
            this.setBackgroundColor(color)
        }!!
        behavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) =
                  onOffsetChanged(bottomSheet, slideOffset, isOpening == true)

            override fun onStateChanged(bottomSheet: View, newState: Int) =
                  handleStateChange(bottomSheet, newState)

        })
/*
        setOnClickListener {
            if (behavior.state == TopSheetBehavior.STATE_COLLAPSED) {
                behavior.state = TopSheetBehavior.STATE_EXPANDED
            }
        }
*/
        setOnTouchListener { v, event ->
            if (behavior.state == TopSheetBehavior.STATE_COLLAPSED) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handleStateChange(this, TopSheetBehavior.STATE_DRAGGING)
                    }
                    MotionEvent.ACTION_UP -> {
                        handleStateChange(this, TopSheetBehavior.STATE_COLLAPSED)
                    }
                }
            }
            false
        }
    }

    private fun handleStateChange(bottomSheet: View, newState: Int) {
        updateBackground(newState)
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                imageview_icon_expand.isInvisible = false
                imageview_handle.isInvisible = true
            }
            else -> {
                imageview_icon_expand.isInvisible = true
                imageview_handle.isInvisible = false
            }
        }
    }

    private fun updateBackground(newState: Int) = context?.let {
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                surfaceColorAnimator.reverse()
                animationRequired = true
            }
            else -> {
                if (animationRequired) {
                    surfaceColorAnimator.start()
                    animationRequired = false
                }
            }
        }
    }

    private fun onOffsetChanged(bottomSheet: View, slideOffset: Float, opening: Boolean) {
        WakaLog.d("onOffsetChanged(opening=$opening)")
        val toolbarAlpha = 1 - (slideOffset / toolbarSlideOffsetBoundary).coerceAtMost(1f)
        textview_summary_current_date.alpha = toolbarAlpha
        imageview_icon_expand.alpha = toolbarAlpha
        imageview_handle.alpha = toolbarAlpha
        bottomSheet.elevation = (finalElevation * slideOffset).coerceAtLeast(if (opening) initialElevation else 0f)
    }

}
