package com.kondenko.pocketwaka.screens.summary

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner.THIS_MONTH
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kizitonwose.calendarview.utils.yearMonth
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.summary.model.AvailableRange
import com.kondenko.pocketwaka.ui.TopSheetBehavior
import com.kondenko.pocketwaka.utils.WakaLog
import com.kondenko.pocketwaka.utils.date.contains
import com.kondenko.pocketwaka.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_content.*
import kotlinx.android.synthetic.main.fragment_date_picker.*
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import kotlinx.android.synthetic.main.item_calendar_month.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields
import java.util.*

class FragmentDatePicker : Fragment(R.layout.fragment_date_picker) {

    companion object {

        private val FONT_DAY_ACTIVE = Typeface.create("sans-serif", Typeface.NORMAL)

        private val FONT_DAY_INACTIVE = Typeface.create("sans-serif-light", Typeface.NORMAL)

    }

    private val vm: DatePickerViewModel by sharedViewModel()

    // UI

    private val surfaceColorResting = R.color.color_app_bar_resting

    private val surfaceColorElevated = R.color.color_app_bar_elevated

    private val initialElevation by lazy {
        resources.getDimension(R.dimen.elevation_datepicker_min)
    }

    private val finalElevation = 32f

    private val toolbarSlideOffsetBoundary = 0.5f

    private lateinit var surfaceColorAnimator: ValueAnimator

    private var isToolbarColorAnimationRequired = true

    private val bottomSheetView by lazy {
        view?.findViewWithParent { it is CoordinatorLayout }
    }

    private lateinit var contentViews: Array<View>

    private val scrimBottomNav by lazy { requireActivity().view_scrim_bottom_nav }

    private val scrimContent by lazy { requireActivity().view_scrim_content }

    // Logic

    private val firstYear = 2013 // The year Wakatime was created

    private val firstMonth = 1

    private val currentMonth = YearMonth.now()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // UI
        val behavior = bottomSheetView?.setupBottomSheetBehavior()

        forEachNonNull(scrimBottomNav, scrimContent) {
            it.setOnClickListener { behavior?.dismiss() }
        }
        contentViews = arrayOf(scrimBottomNav, scrimContent, calendar_datepicker, buttonDatePickerApply, textViewDatePickerLimitedCaption).onEach {
            it.isInvisible = true
        }

        buttonDatePickerApply.setOnClickListener {
            confirmDateSelection()
            behavior?.dismiss()
        }

        // Observers
        vm.availableRangeChanges().observe(viewLifecycleOwner) { availableRange ->
            imageview_icon_expand.isVisible = true
            setupCalendar(behavior, requireView().context, availableRange)
            vm.dataSelectionEvents().takeUnless { it.hasObservers() }?.observe(viewLifecycleOwner) {
                // (secondary) TODO Only update changed days
                calendar_datepicker.notifyCalendarChanged()
            }
            vm.closeEvents().takeUnless { it.hasObservers() }?.observe(viewLifecycleOwner) {
                behavior?.state = TopSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    private fun View.setupBottomSheetBehavior() = TopSheetBehavior.from(this).also { behavior ->
        isClickable = true
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.state_list_animator_date_picker)
        surfaceColorAnimator = createColorAnimator(context, surfaceColorResting, surfaceColorElevated, resources.getInteger(R.integer.duration_datepicker_color_anim).toLong()) { color ->
            activity?.window?.statusBarColor = color
            background.setTint(color)
        }!!
        behavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float, isOpening: Boolean?) =
                  onOffsetChanged(bottomSheet, slideOffset, isOpening == true)

            override fun onStateChanged(bottomSheet: View, newState: Int) =
                  handleStateChange(newState)

        })
        setOnClickListener {
            if (behavior.state == TopSheetBehavior.STATE_COLLAPSED) {
                behavior.state = TopSheetBehavior.STATE_EXPANDED
            }
        }
        setOnTouchListener { v, event ->
            if (behavior.state == TopSheetBehavior.STATE_COLLAPSED) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handleStateChange(TopSheetBehavior.STATE_DRAGGING)
                        elevation = initialElevation
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        if (isToolbarColorAnimationRequired) {
                            handleStateChange(TopSheetBehavior.STATE_COLLAPSED)
                            elevation = 0f
                        }
                    }
                }
            }
            false
        }
    }

    private fun setupCalendar(
          behavior: TopSheetBehavior<*>?,
          context: Context,
          availableRange: AvailableRange?
    ) = with(calendar_datepicker) {
        val firstDayOfWeek = WeekFields.of(context.getCurrentLocale()).firstDayOfWeek
        val startMonth: YearMonth = when (availableRange) {
            is AvailableRange.Limited -> availableRange.date.start.yearMonth
            else -> YearMonth.of(firstYear, firstMonth)
        }
        setup(startMonth = startMonth, endMonth = currentMonth, firstDayOfWeek = firstDayOfWeek)
        scrollToMonth(currentMonth)
        setOnTouchListener { view, event ->
            // Propagate touch events to the bottom sheet when it's collapsed
            behavior == null || behavior.state == TopSheetBehavior.STATE_COLLAPSED
        }
        // (secondary) TODO Extract binders into separate files
        dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, day: CalendarDay) = bindDay(container, day, availableRange)
        }
        monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) = bindMonth(container, month)
        }
    }

    private fun bindDay(container: DayViewContainer, day: CalendarDay, availableRange: AvailableRange?) = with(container.textViewDay) {
        text = day.date.dayOfMonth.toString()
        val isValidPastDate = day.date.run {
            month.value < currentMonth.monthValue && year == currentMonth.year
                  || year < currentMonth.year
        }
        val isValidInCurrentMonth = day.date.run {
            monthValue == currentMonth.monthValue
                  && dayOfMonth <= vm.today.date.dayOfMonth
                  && year == currentMonth.year
        }
        val isUnlocked = when (availableRange) {
            is AvailableRange.Unlimited -> true
            is AvailableRange.Limited -> day.date in availableRange.date
            else -> false
        }
        isActivated = if (isUnlocked) day.run { owner == THIS_MONTH } else false
        typeface = if (isActivated) FONT_DAY_ACTIVE else FONT_DAY_INACTIVE
        isEnabled = (isValidPastDate || isValidInCurrentMonth) && isUnlocked
        val drawableLevel = when (vm.getSelectionState(day)) {
            DaySelectionState.Single -> {
                isSelected = true
                4
            }
            DaySelectionState.Start -> {
                isSelected = true
                2
            }
            DaySelectionState.End -> {
                isSelected = true
                3
            }
            DaySelectionState.Middle -> {
                isSelected = false
                1
            }
            DaySelectionState.Unselected -> {
                isSelected = false
                0
            }
        }
        background = (context.drawable(R.drawable.background_calendar_day) as? LevelListDrawable)?.apply {
            level = drawableLevel
        }
        setOnClickListener {
            vm.onDayClicked(day)
        }
    }

    private fun bindMonth(container: MonthViewContainer, month: CalendarMonth) {
        val formatter = getMonthYearFormat(month.year, currentMonth.year)
        val date = Date(currentMonth.year, month.month - 1, 1)
        val monthName = formatter.format(date).capitalize(Locale.getDefault())
        container.textViewMonth.text = monthName
    }

    fun setTitle(title: String) {
        textview_summary_current_date.text = title
    }

    private fun confirmDateSelection() {
        vm.dateToScrollTo?.let { date ->
            calendar_datepicker.scrollToMonth(YearMonth.of(date.year, date.month))
        }
        vm.confirmDateSelection()
    }

    private fun handleStateChange(newState: Int) {
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                imageview_icon_expand.isInvisible = false
                imageview_handle.isInvisible = true
                /*
                    These onOffsetChanged are required here because when the user opens the app
                    and taps the top sheet for the first time, onOffsetChanged is only called once
                    by onSlide with some negative offset value. These two calls assure
                    that the content views have correct alpha values when opening the top sheet.
                 */
                onOffsetChanged(bottomSheetView!!, 0f, false)
            }
            TopSheetBehavior.STATE_EXPANDED -> {
                imageview_handle.isInvisible = false
                onOffsetChanged(bottomSheetView!!, 1f, true)
            }
            TopSheetBehavior.STATE_DRAGGING, TopSheetBehavior.STATE_SETTLING -> {
                imageview_handle.isInvisible = false
            }
            else -> {
                imageview_icon_expand.isInvisible = true
                imageview_handle.isInvisible = false
            }
        }
        updateBackground(newState)
    }

    private fun updateBackground(newState: Int) = context?.let {
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                showScrim(false)
                if (isToolbarColorAnimationRequired) {
                    surfaceColorAnimator.reverse()
                    isToolbarColorAnimationRequired = true
                }
            }
            else -> {
                showScrim(true)
                if (isToolbarColorAnimationRequired) {
                    surfaceColorAnimator.start()
                    isToolbarColorAnimationRequired = false
                }
            }
        }
    }

    private fun onOffsetChanged(bottomSheet: View, slideOffset: Float, isOpening: Boolean) {
        WakaLog.d("onOffsetChanged(slideOffset = $slideOffset, isOpening = $isOpening)")
        val toolbarAlpha = 1 - (slideOffset / toolbarSlideOffsetBoundary).coerceAtMost(1f)
        val contentAlpha = (-2 * (toolbarSlideOffsetBoundary - slideOffset)).coerceAtLeast(0f)
        forEach(textview_summary_current_date, imageview_icon_expand) {
            it?.alpha = toolbarAlpha
        }
        forEach(*contentViews) {
            val hideLimitedDatesCaption = it?.id == R.id.textViewDatePickerLimitedCaption && vm.isStatsRangeUnlimited
            it?.alpha = if (hideLimitedDatesCaption) 0f else contentAlpha
            it?.isVisible = true
        }
        bottomSheet.elevation = (finalElevation * slideOffset)
              .coerceAtLeast(if (isOpening && slideOffset > 0f) initialElevation else 0f)
        animateBackground(slideOffset, isOpening)
    }

    private fun animateBackground(fraction: Float, isOpening: Boolean) {
        surfaceColorAnimator.setCurrentFraction(fraction)
        isToolbarColorAnimationRequired = isOpening
    }

    private fun TopSheetBehavior<*>.dismiss() {
        state = TopSheetBehavior.STATE_COLLAPSED
    }

    private fun showScrim(show: Boolean) = forEach(scrimBottomNav, scrimContent) {
        it?.isGone = !show
    }

    enum class DaySelectionState {
        Unselected, Single, Start, Middle, End
    }

    private inner class DayViewContainer(view: View) : ViewContainer(view) {
        val textViewDay: TextView = view.textview_calendar_day
    }

    private inner class MonthViewContainer(view: View) : ViewContainer(view) {
        val textViewMonth: TextView = view.textview_calendar_month
    }

}
