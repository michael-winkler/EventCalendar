package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.adapter.EventsAdapter
import com.nmd.eventCalendar.databinding.EcvEventCalendarSingleWeekViewBinding
import com.nmd.eventCalendar.databinding.EcvTextviewCircleBinding
import com.nmd.eventCalendar.`interface`.EventCalendarDayClickListener
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.expressiveCwHelper
import com.nmd.eventCalendar.utils.Utils.Companion.getCurrentMonth
import com.nmd.eventCalendar.utils.Utils.Companion.getCurrentWeekNumber
import com.nmd.eventCalendar.utils.Utils.Companion.getCurrentYear
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysForCurrentWeek
import com.nmd.eventCalendar.utils.Utils.Companion.getDimensInt
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.getRealContext
import com.nmd.eventCalendar.utils.Utils.Companion.getTextTypeface
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList
import com.nmd.eventCalendar.utils.Utils.Companion.setItemTint
import java.util.Calendar

@Suppress("unused")
class EventCalendarSingleWeekView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    private val binding =
        EcvEventCalendarSingleWeekViewBinding.inflate(LayoutInflater.from(context))

    private var clickListener: EventCalendarDayClickListener? = null
    private var eventArrayList: ArrayList<Event> = ArrayList()

    // For xml layout
    internal var headerVisible = true
    private var isCalendarWeekVisible = false

    // Current day
    internal var currentWeekdayTextColor =
        ContextCompat.getColor(getContext(), R.color.ecv_item_day_name_color)
    internal var currentDayBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_black)
    internal var currentDayTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)

    // Count background
    internal var countBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_black)
    internal var countBackgroundTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var countVisible = true

    // Event item
    internal var eventItemAutomaticTextColor = true
    internal var eventItemTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var eventItemDarkTextColor =
        ContextCompat.getColor(getContext(), R.color.ecv_black)

    // Expressive UI
    internal var isExpressiveUi = false
    internal var expressiveCwBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_expressive_cw_background_color)
    internal var expressiveDayBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_expressive_day_background_color)

    init {
        getContext().withStyledAttributes(attrs, R.styleable.EventCalendarSingleWeekView) {
            headerVisible =
                getBoolean(
                    R.styleable.EventCalendarSingleWeekView_ecv_header_visible,
                    headerVisible
                )
            isCalendarWeekVisible = getBoolean(
                R.styleable.EventCalendarSingleWeekView_ecv_calendar_week_visible,
                isCalendarWeekVisible
            )
            currentWeekdayTextColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_current_weekday_text_color),
                currentWeekdayTextColor
            )
            currentDayBackgroundTintColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_current_day_background_tint_color),
                currentDayBackgroundTintColor
            )
            currentDayTextColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_current_day_text_color),
                currentDayTextColor
            )
            countBackgroundTintColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_count_background_tint_color),
                countBackgroundTintColor
            )
            countBackgroundTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_count_background_text_color),
                countBackgroundTextColor
            )
            countVisible =
                getBoolean(R.styleable.EventCalendarSingleWeekView_ecv_count_visible, countVisible)
            eventItemAutomaticTextColor = getBoolean(
                R.styleable.EventCalendarSingleWeekView_ecv_event_item_automatic_text_color,
                eventItemAutomaticTextColor
            )
            eventItemTextColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_event_item_text_color),
                eventItemTextColor
            )
            eventItemDarkTextColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_event_item_dark_text_color),
                eventItemDarkTextColor
            )
            isExpressiveUi =
                getBoolean(
                    R.styleable.EventCalendarSingleWeekView_ecv_expressive_ui,
                    isExpressiveUi
                )
            expressiveCwBackgroundTintColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_expressive_cw_background_tint_color),
                expressiveCwBackgroundTintColor
            )
            expressiveDayBackgroundTintColor = getColor(
                (R.styleable.EventCalendarSingleWeekView_ecv_expressive_day_background_tint_color),
                expressiveDayBackgroundTintColor
            )
        }

        addView(binding.root)

        // We want a initial calendar week ui. That is the reason why we update the layout.
        // Without the call we see a empty view.
        updateLayout()
    }

    /**
     * Set a [EventCalendarDayClickListener] to the [EventCalendarView].
     * You can also insert null to remove the listener.
     * @param eventCalendarDayClickListener [EventCalendarDayClickListener]?
     */
    fun addOnDayClickListener(eventCalendarDayClickListener: EventCalendarDayClickListener?) {
        clickListener = eventCalendarDayClickListener
    }

    /**
     * Set events to the [EventCalendarView].
     * The [Event] model looks like this:
     * ```
     * @Parcelize
     * data class Event(
     *     val date: String,
     *     val name: String,
     *     val backgroundHexColor: String,
     *     val data: @RawValue Any? = null,
     * ) : Parcelable
     * ```
     *
     * You can also insert here:
     * ```
     * val data: @RawValue Any? = null,
     * ```
     * your custom model but make sure you declare it as [kotlinx.parcelize.Parcelize].
     *
     * If you do not want to use a model you can also use [String] or [Int] or any else you want as data.
     *
     * Also if there are more events on a single day than the user can visibly see,
     * the last fully visible item will be automatically displayed as "+1", for example.
     * In this case, the count text is automatically generated. If you don't want the automatic count
     * then you can disable it in your xml-layout
     * ```
     * app:ecv_count_visible="false"
     * ```
     * The [Event] item text color is set automatic to light or dark.
     * This depends on the [Event] item "backgroundHexColor". If it is a dark background color,
     * then the text color will be light, else dark. If you don't want the automatic text color
     * then you can disable it in your xml-layout
     * ```
     * app:ecv_event_item_automatic_text_color="false"
     * ```
     * You can set now the event item text color like this in your xml-layout
     * ```
     * app:ecv_event_item_text_color="@android:color/white"
     * ```
     * but remember that "ecv_event_item_text_color" only works if "ecv_event_item_automatic_text_color"
     * is set to false!
     *
     * @return The current event list. Can not be null but empty list.
     */
    var events: ArrayList<Event>
        get() {
            return eventArrayList
        }
        set(events) {
            eventArrayList = events
            updateLayout()
        }

    /**
     * Set this to true if you want to display the calendar week on each week row.
     *
     * You can also set this value inside your xml layout:
     * ```
     * app:ecv_calendar_week_visible="true"
     * ```
     * Default is "false"
     */
    var calendarWeekVisible: Boolean
        get() = isCalendarWeekVisible
        set(value) {
            isCalendarWeekVisible = value
            updateLayout()
        }

    /**
     * Indicates whether to display the expressive UI.
     *
     * Set this property to `true` to display the expressive UI. By default, this is `false`.
     *
     * You can also set this value in your XML layout as follows:
     * ```
     * app:ecv_expressive_ui="true"
     * ```
     */
    var expressiveUi: Boolean
        get() = isExpressiveUi
        set(value) {
            isExpressiveUi = value
            updateLayout()
        }

    /**
     * Internal method.
     */
    @SuppressLint("NotifyDataSetChanged")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun updateLayout(): Unit = with(binding) {
        val monthYearText = "${getCurrentMonth().getMonthName(context)} ${getCurrentYear()}"
        eventCalendarSingleWeekViewMonthYearTextView1.text = monthYearText

        eventCalendarSingleWeekViewMonthYearHeader.post {
            eventCalendarSingleWeekViewMonthYearHeader.isVisible = headerVisible

            if (isCalendarWeekVisible) {
                eventCalendarViewCalendarWeek.eventCalendarViewDayTextView.text =
                    "${getCurrentWeekNumber()}"
            }

            if (isExpressiveUi) {
                eventCalendarViewLinearLayoutCompat.showDividers =
                    LinearLayoutCompat.SHOW_DIVIDER_NONE

                eventCalendarSingleWeekViewRowsLinearLayoutCompat.showDividers =
                    LinearLayoutCompat.SHOW_DIVIDER_NONE
            } else {
                eventCalendarViewLinearLayoutCompat.showDividers =
                    LinearLayoutCompat.SHOW_DIVIDER_BEGINNING or LinearLayoutCompat.SHOW_DIVIDER_MIDDLE or LinearLayoutCompat.SHOW_DIVIDER_END

                eventCalendarSingleWeekViewRowsLinearLayoutCompat.showDividers =
                    LinearLayoutCompat.SHOW_DIVIDER_BEGINNING or LinearLayoutCompat.SHOW_DIVIDER_MIDDLE or LinearLayoutCompat.SHOW_DIVIDER_END
            }

            eventCalendarViewHeaderKw.isVisible = isCalendarWeekVisible
            eventCalendarViewCalendarWeek.root.isVisible = isCalendarWeekVisible
        }

        initTextViews(
            days = getDaysForCurrentWeek(),
            materialTextView = eventCalendarViewCalendarWeek.eventCalendarViewDayTextView,
            frameLayout = eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewExpressiveFrameLayout,
            linearLayoutCompat = eventCalendarViewCalendarWeek.root
        )

        val currentDayView = when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> eventCalendarViewHeaderMonday
            Calendar.TUESDAY -> eventCalendarViewHeaderTuesday
            Calendar.WEDNESDAY -> eventCalendarViewHeaderWednesday
            Calendar.THURSDAY -> eventCalendarViewHeaderThursday
            Calendar.FRIDAY -> eventCalendarViewHeaderFriday
            Calendar.SATURDAY -> eventCalendarViewHeaderSaturday
            else -> {
                eventCalendarViewHeaderSunday
            }
        }

        currentDayView.setTypeface(currentDayView.typeface, Typeface.BOLD)
        currentDayView.setTextColor(currentWeekdayTextColor)
    }

    private fun initTextViews(
        days: List<Day>,
        materialTextView: MaterialTextView,
        frameLayout: FrameLayout,
        linearLayoutCompat: LinearLayoutCompat
    ): Unit = with(binding) {
        val bindingArrayList = arrayListOf(
            eventCalendarSingleWeekViewDay1,
            eventCalendarSingleWeekViewDay2,
            eventCalendarSingleWeekViewDay3,
            eventCalendarSingleWeekViewDay4,
            eventCalendarSingleWeekViewDay5,
            eventCalendarSingleWeekViewDay6,
            eventCalendarSingleWeekViewDay7
        )

        if (days.size != bindingArrayList.size) {
            throw RuntimeException("Day-List and Bindings-List can not be different!")
        }

        val eventArrayList1 = ArrayList<Event>()
        for (day in days) {
            eventArrayList1.addAll(eventArrayList.filter { it.date == day.date })
        }

        styleTextViews(
            days = days,
            list = bindingArrayList,
            eventsList = ArrayList(eventArrayList1),
            materialTextView = materialTextView,
            frameLayout = frameLayout
        )

        bindingArrayList.forEach {
            if (isExpressiveUi) {
                it.root.showDividers = LinearLayoutCompat.SHOW_DIVIDER_NONE
            } else {
                it.root.showDividers = LinearLayoutCompat.SHOW_DIVIDER_BEGINNING
            }
        }

        linearLayoutCompat.showDividers = if (isExpressiveUi) {
            LinearLayoutCompat.SHOW_DIVIDER_NONE
        } else {
            LinearLayoutCompat.SHOW_DIVIDER_BEGINNING
        }
    }

    private fun styleTextViews(
        days: List<Day>,
        list: List<EcvTextviewCircleBinding>,
        eventsList: ArrayList<Event>,
        materialTextView: MaterialTextView,
        frameLayout: FrameLayout
    ) {
        for (index in days.indices) {
            val day = days[index]
            val dayItemLayout = list[index]

            with(dayItemLayout.eventCalendarViewDayLinearLayoutCompat) {
                setOnClickListener {
                    clickListener?.onClick(day)
                }

                context.getRealContext()?.let {
                    if (expressiveUi) {
                        val ripple = ContextCompat.getDrawable(
                            context, when (index) {
                                0 -> {
                                    // First day
                                    R.drawable.ecv_ripple_expressive_single_left
                                }

                                6 -> {
                                    // Last day
                                    R.drawable.ecv_ripple_expressive_single_right
                                }

                                else -> {
                                    // Default rounded
                                    R.drawable.ecv_ripple_expressive_default
                                }
                            }
                        ) as RippleDrawable
                        ripple.setItemTint(
                            expressiveDayBackgroundTintColor
                        )

                        background = ripple
                    } else {
                        background = ContextCompat.getDrawable(
                            it,
                            R.drawable.ecv_ripple_default
                        )
                    }
                }
            }

            val textView = dayItemLayout.eventCalendarViewDayTextView

            val eventList = day.dayEvents(eventsList.orEmptyArrayList())
            val recyclerView = dayItemLayout.eventCalendarViewDayRecyclerView
            with(recyclerView) {
                suppressLayout(true)
                addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        return true
                    }
                })
                setItemViewCacheSize(100)
                setHasFixedSize(true)
                isSaveEnabled = false
                itemAnimator = null
            }

            if (countVisible && eventList.isNotEmpty()) {
                recyclerView.addItemDecoration(
                    LastPossibleVisibleItemForUserDecoration(
                        eventList
                    )
                )
            }

            if (eventList.isNotEmpty()) {
                recyclerView.adapter = EventsAdapter(
                    list = eventList,
                    eventItemAutomaticTextColor = eventItemAutomaticTextColor,
                    eventItemTextColor = eventItemTextColor,
                    eventItemDarkTextColor = eventItemDarkTextColor
                )
            }

            with(textView) {
                text = day.value

                if (day.isCurrentDay) {
                    setTextColor(currentDayTextColor)

                    context.getRealContext()?.let {
                        background = ContextCompat.getDrawable(
                            it,
                            if (expressiveUi) R.drawable.ecv_expressive_circle else R.drawable.ecv_circle
                        )
                    }

                    ViewCompat.setBackgroundTintList(
                        this, ColorStateList.valueOf(currentDayBackgroundTintColor)
                    )
                }

                updateLayoutParams {
                    height = if (expressiveUi) {
                        LayoutParams.WRAP_CONTENT
                    } else {
                        R.dimen.dp_current_day_height.getDimensInt(context.resources)
                    }
                }

                setTypeface(typeface, day.getTextTypeface())
            }

            if (index == 0 && isCalendarWeekVisible) {
                expressiveUi.expressiveCwHelper(
                    frameLayout = frameLayout,
                    index = -1,
                    cwBackgroundTintColor = expressiveCwBackgroundTintColor
                )
            }
        }
    }

    inner class LastPossibleVisibleItemForUserDecoration(private val eventList: ArrayList<Event>) :
        RecyclerView.ItemDecoration() {
        @SuppressLint("SetTextI18n")
        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            super.onDraw(c, parent, state)
            if (eventList.isEmpty()) return

            val layoutManager = parent.layoutManager as? LinearLayoutManager ?: return
            val lastCompleteVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

            if (lastCompleteVisiblePosition == RecyclerView.NO_POSITION || lastCompleteVisiblePosition == eventList.lastIndex) return

            val count = eventList.size - lastCompleteVisiblePosition - 1
            if (count > 0) {
                val materialTextView =
                    parent.findViewHolderForAdapterPosition(lastCompleteVisiblePosition)?.itemView as? MaterialTextView
                materialTextView?.let { textView ->
                    textView.text = "+${count.plus(1)}"
                    textView.setTextColor(countBackgroundTextColor)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)

                    ViewCompat.setBackgroundTintList(
                        textView, ColorStateList.valueOf(countBackgroundTintColor)
                    )
                }
            }

            for (i in lastCompleteVisiblePosition + 1..eventList.lastIndex) {
                val view = parent.findViewHolderForAdapterPosition(i)?.itemView ?: continue
                view.isVisible = false
            }
        }
    }

}