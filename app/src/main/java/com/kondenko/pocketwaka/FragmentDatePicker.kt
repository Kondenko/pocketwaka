package com.kondenko.pocketwaka

import android.animation.AnimatorInflater
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.LevelListDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.forEach
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.kondenko.pocketwaka.screens.summary.SummaryRangeViewModel
import com.kondenko.pocketwaka.ui.TopSheetBehavior
import com.kondenko.pocketwaka.utils.extensions.*
import kotlinx.android.synthetic.main.fragment_date_picker.*
import kotlinx.android.synthetic.main.item_calendar_day.view.*
import kotlinx.android.synthetic.main.item_calendar_month.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.threeten.bp.MonthDay
import org.threeten.bp.YearMonth
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.WeekFields

class DayViewContainer(view: View) : ViewContainer(view) {
    val textViewDay: TextView = view.textview_calendar_day
}

class MonthViewContainer(view: View) : ViewContainer(view) {
    val textViewMonth: TextView = view.textview_calendar_month
}

enum class DaySelectionState {
    Unselected, Single, Start, Middle, End
}

class FragmentDatePicker : Fragment() {

    private val vm: SummaryRangeViewModel by sharedViewModel()

    // UI

    private val surfaceColorResting = R.color.color_window_background

    private val surfaceColorElevated = R.color.color_background_white

    private val initialElevation = 6f

    private val finalElevation = 16f

    private val toolbarSlideOffsetBoundary = 0.5f

    private lateinit var surfaceColorAnimator: ValueAnimator

    private var animationRequired = true

    private lateinit var contentViews: Array<View>

    // Logic

    private val firstYear = 2013 // The year when Wakatime was created

    private val firstMonth = 1

    private val currentMonth = YearMonth.now()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_date_picker, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = view.findViewWithParent { it is CoordinatorLayout }?.setupBottomSheetBehavior()
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
        setupCalendar(behavior, view.context)
        vm.calendarInvalidationEvents().observe(viewLifecycleOwner) {
            calendar_datepicker.notifyCalendarChanged()
        }
    }

    private fun setupButtons() {
        button_summary_today.setOnClickListener {
            setButtonSelected(it)
        }
        button_summary_yesterday.setOnClickListener {
            setButtonSelected(it)
        }
        button_summary_this_week.setOnClickListener {
            setButtonSelected(it)
        }
        button_summary_last_week.setOnClickListener {
            setButtonSelected(it)
        }
        // TODO Lock for free accounts
        button_summary_this_month.setOnClickListener {
            setButtonSelected(it)
        }
        button_summary_last_month.setOnClickListener {
            setButtonSelected(it)
        }
    }

    private fun setupCalendar(behavior: TopSheetBehavior<*>?, context: Context) = with(calendar_datepicker) {
        val currentDay = MonthDay.now()
        val firstDayOfWeek = WeekFields.of(context.getCurrentLocale()).firstDayOfWeek
        setup(startMonth = YearMonth.of(firstYear, firstMonth), endMonth = currentMonth, firstDayOfWeek = firstDayOfWeek)
        scrollToMonth(currentMonth)
        setOnTouchListener { v, event ->
            behavior == null || behavior.state == TopSheetBehavior.STATE_COLLAPSED
        }
        dayBinder = object : DayBinder<DayViewContainer> {

            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) = bindDay(container, day, currentDay)

        }
        monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {

            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, month: CalendarMonth) = bindMonth(container, month)

        }
    }

    private fun bindDay(container: DayViewContainer, day: CalendarDay, currentDay: MonthDay) = with(container.textViewDay) {
        text = day.date.dayOfMonth.toString()
        val isValidPastDate = day.date.run {
            month.value < currentMonth.monthValue && year == currentMonth.year
                  || year < currentMonth.year
        }
        val isValidInCurrentMonth = day.date.run {
            monthValue == currentMonth.monthValue
                  && dayOfMonth <= currentDay.dayOfMonth
                  && year == currentMonth.year
        }
        isEnabled = isValidPastDate || isValidInCurrentMonth // TODO Also check if in the 2-week range for free accounts
        isActivated = day.owner == com.kizitonwose.calendarview.model.DayOwner.THIS_MONTH
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
        background = (context.getDrawable(com.kondenko.pocketwaka.R.drawable.background_calendar_day) as? LevelListDrawable)?.apply {
            level = drawableLevel
        }
        setOnClickListener { vm.onDayClicked(day) }
    }

    private fun bindMonth(container: MonthViewContainer, month: CalendarMonth) = with(container.textViewMonth) {
        text = month.yearMonth.month.getDisplayName(
              TextStyle.FULL,
              context.getCurrentLocale()
        )
        // TODO Append year if not the current one
    }

    private fun setButtonSelected(view: View) {
        group_datepicker_buttons.forEach {
            it.isSelected = it.id == view.id
        }
    }

    fun setTitle(title: String) {
        textview_summary_current_date.text = title
    }

    private fun View.setupBottomSheetBehavior(): TopSheetBehavior<*> {
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

    private fun handleStateChange(newState: Int) {
        updateBackground(newState)
        when (newState) {
            TopSheetBehavior.STATE_COLLAPSED -> {
                calendar_datepicker.scrollToMonth(currentMonth)
                vm.confirmDateSelection()
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
        val toolbarAlpha = 1 - (slideOffset / toolbarSlideOffsetBoundary).coerceAtMost(1f)
        textview_summary_current_date.alpha = toolbarAlpha
        imageview_icon_expand.alpha = toolbarAlpha
        imageview_handle.alpha = toolbarAlpha
        forEach(*contentViews) {
            it?.isVisible = true
            it?.alpha = 1 - toolbarAlpha
        }
        bottomSheet.elevation = (finalElevation * slideOffset)
              .coerceAtLeast(if (opening && slideOffset > 0f) initialElevation else 0f)
    }

}
