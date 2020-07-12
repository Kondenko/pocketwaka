package com.kondenko.pocketwaka.screens.summary

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.LevelListDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.forEach
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import com.google.android.material.button.MaterialButton
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner.THIS_MONTH
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kondenko.pocketwaka.R
import com.kondenko.pocketwaka.domain.summary.model.AvailableRange
import com.kondenko.pocketwaka.ui.TopSheetBehavior
import com.kondenko.pocketwaka.utils.date.DateRange
import com.kondenko.pocketwaka.utils.date.contains
import com.kondenko.pocketwaka.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_date_picker.*
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import kotlinx.android.synthetic.main.item_calendar_month.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.temporal.WeekFields

// (won't implement for now) TODO Dim background and make it closable by clicking on the outside
// should be the topmost fragment
class FragmentDatePicker : Fragment() {

    companion object {

        private val FONT_DAY_ACTIVE = Typeface.create("sans-serif", Typeface.NORMAL)

        private val FONT_DAY_INACTIVE = Typeface.create("sans-serif-light", Typeface.NORMAL)

    }

    private val vm: SummaryRangeViewModel by sharedViewModel()

    // UI

    private val predefinedRangeToButtonIdMap = mapOf(
          DateRange.PredefinedRange.Today.range to R.id.button_summary_today,
          DateRange.PredefinedRange.Yesterday.range to R.id.button_summary_yesterday,
          DateRange.PredefinedRange.ThisWeek.range to R.id.button_summary_this_week,
          DateRange.PredefinedRange.LastWeek.range to R.id.button_summary_last_week,
          DateRange.PredefinedRange.ThisMonth.range to R.id.button_summary_this_month,
          DateRange.PredefinedRange.LastMonth.range to R.id.button_summary_last_month
    )

    private val surfaceColorResting = R.color.color_app_bar_resting

    private val surfaceColorElevated = R.color.color_app_bar_elevated

    private val initialElevation = 6f

    private val finalElevation = 32f

    private val toolbarSlideOffsetBoundary = 0.5f

    private lateinit var surfaceColorAnimator: ValueAnimator

    private var animationRequired = true

    private lateinit var contentViews: Array<View>

    // Logic

    private val firstYear = 2013 // The year Wakatime was created

    private val firstMonth = 1

    private val currentMonth = YearMonth.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
          inflater.inflate(R.layout.fragment_date_picker, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetView = view.findViewWithParent { it is CoordinatorLayout }
        val behavior = bottomSheetView?.setupBottomSheetBehavior()
        contentViews = arrayOf(
              button_summary_today,
              button_summary_yesterday,
              button_summary_this_week,
              button_summary_last_week,
              button_summary_this_month,
              button_summary_last_month,
              calendar_datepicker
        )
        contentViews.forEach { it.isVisible = false }
        setupButtons()
        setupCalendar(behavior, view.context, null)
        vm.dataSelectionEvents().observe(viewLifecycleOwner) { selectedDate ->
            // (secondary) TODO Only update changed days
            calendar_datepicker.notifyCalendarChanged()
            val buttonToSelect = predefinedRangeToButtonIdMap[selectedDate]
            setButtonSelected(buttonToSelect?.let { view.findViewById<Button>(it) })
        }
        vm.closeEvents().observe(viewLifecycleOwner) {
            behavior?.state = TopSheetBehavior.STATE_COLLAPSED
        }
        vm.availableRangeChanges().observe(viewLifecycleOwner) { availableRange ->
            imageview_icon_expand.isVisible = true
            setupCalendar(behavior, view.context, availableRange)
            val isRangeLimited = availableRange != AvailableRange.Unlimited
            forEach(button_summary_last_month, button_summary_this_month) {
                // (secondary) TODO Unlock button_summary_this_month if today is less that 2 weeks away from start of month
                it?.lock(isRangeLimited)
            }
        }
    }

    private fun MaterialButton.lock(lock: Boolean) {
        isEnabled = !lock
        // TODO Test on pre 8.0 Android versions
        val drawableStart = if (lock) context.drawable(R.drawable.ic_date_locked) else null
        icon = drawableStart
    }

    private fun View.setupBottomSheetBehavior(): TopSheetBehavior<*> {
        val behavior = TopSheetBehavior.from(this)
        isClickable = true
        stateListAnimator = AnimatorInflater.loadStateListAnimator(context, R.animator.state_list_animator_date_picker)
        surfaceColorAnimator = createColorAnimator(
              context,
              surfaceColorResting,
              surfaceColorElevated,
              resources.getInteger(R.integer.duration_datepicker_color_anim).toLong()
        ) { color ->
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
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        handleStateChange(TopSheetBehavior.STATE_COLLAPSED)
                        elevation = 0f
                    }
                }
            }
            false
        }
        return behavior
    }

    private fun setupButtons() {
        fun onClick(view: View) {
            predefinedRangeToButtonIdMap
                  .entries
                  .find { (_, id) -> view.id == id }
                  ?.key // date
                  ?.let(vm::onButtonClicked)
        }
        button_summary_today.setOnClickListener(::onClick)
        button_summary_yesterday.setOnClickListener(::onClick)
        button_summary_this_week.setOnClickListener(::onClick)
        button_summary_last_week.setOnClickListener(::onClick)
        button_summary_this_month.setOnClickListener(::onClick)
        button_summary_last_month.setOnClickListener(::onClick)
    }

    private fun setupCalendar(
          behavior: TopSheetBehavior<*>?,
          context: Context,
          availableRange: AvailableRange?
    ) = with(calendar_datepicker) {
        val firstDayOfWeek = WeekFields.of(context.getCurrentLocale()).firstDayOfWeek
        setup(startMonth = YearMonth.of(firstYear, firstMonth), endMonth = currentMonth, firstDayOfWeek = firstDayOfWeek)
        scrollToMonth(currentMonth)
        setOnTouchListener { view, event ->
            // Propagate touch events to the bottom sheet when it's collapsed
            behavior == null || behavior.state == TopSheetBehavior.STATE_COLLAPSED
        }
        // (secondary) TODO Extract binders into separate files
        dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) =
                  DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) =
                  bindDay(container, day, availableRange)

        }
        monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {

            override fun create(view: View) =
                  MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, month: CalendarMonth) =
                  bindMonth(container, month)

        }
    }

    private fun bindDay(
          container: DayViewContainer,
          day: CalendarDay,
          availableRange: AvailableRange?
    ) = with(container.textViewDay) {
        val today = LocalDate.now()
        text = day.date.dayOfMonth.toString()
        val isValidPastDate = day.date.run {
            month.value < currentMonth.monthValue && year == currentMonth.year
                  || year < currentMonth.year
        }
        val isValidInCurrentMonth = day.date.run {
            monthValue == currentMonth.monthValue
                  && dayOfMonth <= today.dayOfMonth
                  && year == currentMonth.year
        }
        val isUnlocked = when (availableRange) {
            null -> false
            is AvailableRange.Unlimited -> true
            is AvailableRange.Limited -> day.date in availableRange.date
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
        background = (context.getDrawable(R.drawable.background_calendar_day) as? LevelListDrawable)?.apply {
            level = drawableLevel
        }
        setOnClickListener {
            vm.onDayClicked(day)
        }
    }

    private fun bindMonth(
          container: MonthViewContainer,
          month: CalendarMonth
    ) = with(container.textViewMonth) {
        val formatter = month.yearMonth.getMonthYearFormat(currentMonth.year)
        text = month.yearMonth.format(formatter)
    }

    private fun setButtonSelected(view: View?) {
        group_datepicker_buttons.forEach {
            it.isSelected = it.id == view?.id
        }
    }

    fun setTitle(title: String) {
        textview_summary_current_date.text = title
    }

    private fun handleStateChange(newState: Int) {
        updateBackground(newState)
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                vm.dateToScrollTo?.let { date ->
                    calendar_datepicker.scrollToMonth(YearMonth.of(date.year, date.month))
                }
                vm.confirmDateSelection()
                imageview_icon_expand.isInvisible = false
            }
            TopSheetBehavior.STATE_DRAGGING -> {
                textViewDatePickerLimitedCaption.isInvisible = true
                imageview_handle.isInvisible = true
            }
            TopSheetBehavior.STATE_EXPANDED -> {
                textViewDatePickerLimitedCaption.isInvisible = vm.isStatsRangeUnlimited
            }
            TopSheetBehavior.STATE_SETTLING -> {
                imageview_handle.isInvisible = true
                textViewDatePickerLimitedCaption.isInvisible = true
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
                if (animationRequired) {
                    surfaceColorAnimator.reverse()
                    animationRequired = true
                }
            }
            else -> {
                if (animationRequired) {
                    surfaceColorAnimator.start()
                    animationRequired = false
                }
            }
        }
    }

    private fun onOffsetChanged(bottomSheet: View, slideOffset: Float, isOpening: Boolean) {
        val toolbarAlpha = 1 - (slideOffset / toolbarSlideOffsetBoundary).coerceAtMost(1f)
        forEach(textview_summary_current_date, imageview_icon_expand, imageview_handle) {
            it?.alpha = toolbarAlpha
        }
        forEach(*contentViews) {
            it?.isVisible = true
            it?.alpha = 1 - toolbarAlpha
        }
        bottomSheet.elevation = (finalElevation * slideOffset)
              .coerceAtLeast(if (isOpening && slideOffset > 0f) initialElevation else 0f)
        animateBackground(slideOffset, isOpening)
    }

    private fun animateBackground(fraction: Float, isOpening: Boolean) {
        // TODO Bump minSdk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            surfaceColorAnimator.setCurrentFraction(fraction)
            animationRequired = isOpening
        }
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
