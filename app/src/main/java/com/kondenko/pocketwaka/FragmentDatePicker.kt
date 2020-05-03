package com.kondenko.pocketwaka

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import com.kondenko.pocketwaka.ui.TopSheetBehavior
import com.kondenko.pocketwaka.utils.extensions.findViewWithParent
import kotlinx.android.synthetic.main.fragment_date_picker.*

class FragmentDatePicker : Fragment() {

    private val colorBackgroundResting = android.R.color.transparent

    private val colorBackgroundElevated = R.color.color_background_white

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
        behavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) =
                  onOffsetChanged(slideOffset)

            override fun onStateChanged(bottomSheet: View, newState: Int) =
                  handleStateChange(bottomSheet, newState)

        })
        setOnClickListener {
            if (behavior.state == TopSheetBehavior.STATE_COLLAPSED) {
                behavior.state = TopSheetBehavior.STATE_EXPANDED
            }
        }
        view?.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    handleStateChange(this, TopSheetBehavior.STATE_DRAGGING)
                    false
                }
                MotionEvent.ACTION_UP -> true
                else -> false
            }
        }
    }

    private fun handleStateChange(bottomSheet: View, newState: Int) {
        updateBackground(bottomSheet, newState)
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

    private fun updateBackground(bottomSheet: View, newState: Int) {
        val backgroundColor = if (newState == TopSheetBehavior.STATE_COLLAPSED) {
            colorBackgroundResting
        } else {
            colorBackgroundElevated
        }
        bottomSheet.setBackgroundResource(backgroundColor)
    }

    private fun onOffsetChanged(slideOffset: Float) {
        val toolbarAlpha = 1 - slideOffset.coerceAtLeast(0f)
        textview_summary_current_date.alpha = toolbarAlpha
        imageview_icon_expand.alpha = toolbarAlpha
        imageview_handle.alpha = toolbarAlpha
    }

}
