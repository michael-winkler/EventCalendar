package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.nmd.eventCalendar.adapter.InfiniteAdapter
import com.nmd.eventCalendar.custom.SnapOnScrollListener
import com.nmd.eventCalendar.custom.SnapOnScrollListener.Companion.attachSnapHelperWithListener
import com.nmd.eventCalendar.databinding.EcvEventCalendarBinding
import com.nmd.eventCalendar.`interface`.EventCalendarDayClickListener
import com.nmd.eventCalendar.`interface`.EventCalendarScrollListener
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.state.InstanceState
import com.nmd.eventCalendar.utils.Utils.Companion.getActivity
import com.nmd.eventCalendar.utils.Utils.Companion.smoothScrollTo
import java.time.YearMonth
import java.util.*
import kotlin.math.abs

/**
 * Provides a [EventCalendarView].
 *
 * You can create a new [EventCalendarView] inside your XML-Layout like this:
 * ```
 * <com.nmd.eventCalendar.EventCalendarView
 *     android:id="@+id/eventCalendarView"
 *     android:layout_width="match_parent"
 *     android:layout_height="match_parent"
 *     app:ecv_count_background_text_color="@android:color/white"
 *     app:ecv_count_background_tint_color="@android:color/holo_blue_light"
 *     app:ecv_count_visible="true"
 *     app:ecv_current_day_background_tint_color="@android:color/holo_red_dark"
 *     app:ecv_current_day_text_color="@android:color/white"
 *     app:ecv_disallow_intercept="false"
 *     app:ecv_header_visible="true"
 *     app:ecv_calendar_week_visible="false"/>
 * ```
 *
 * Now in your class you can get a reference to it like this:
 * ```
 * binding.eventCalendarView.addOnDayClickListener(object :
 *     EventCalendarDayClickListener {
 *     override fun onClick(day: Day) {
 *         // val eventList = binding.eventCalendarView.events.filter { it.date == day.date }
 *         // You can use this to get the events for the selected day
 *         Log.i("ECV", "TEST 1: " + day.date)
 *     }
 * })
 * ```
 * It is really to easy to add events to the calendar.
 * Here is an example code:
 * ```
 * binding.eventCalendarView.events = arrayListOf(
 *     Event(date = "15.04.2023", name = "Vacation", backgroundHexColor = "#4badeb"),
 *     Event(date = "16.04.2023", name = "Home office", backgroundHexColor = "#e012ad"),
 *     Event(date = "17.04.2023", name = "Meeting", backgroundHexColor = "#e07912"),
 *     Event(date = "18.04.2023", name = "Vacation", backgroundHexColor = "#4badeb", data = "Let's go!")
 * )
 * ```
 * The date format have to be in format "dd.MM.yyyy".
 * For more details about how a event model should be
 * @see events
 *
 */
class EventCalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    internal val binding = EcvEventCalendarBinding.inflate(LayoutInflater.from(context))

    internal var currentRecyclerViewPosition = 0
    private val currentCalendar = Calendar.getInstance()
    private val currentYear = currentCalendar.get(Calendar.YEAR)
    private val currentMonth = currentCalendar.get(Calendar.MONTH)

    private lateinit var currentYearAndMonthPair: Pair<Int, Int>

    internal var sMonth = Calendar.JANUARY
    internal var sYear = 2020

    internal var eMonth = Calendar.DECEMBER
    internal var eYear = currentYear.plus(1)

    internal var clickListener: EventCalendarDayClickListener? = null
    private var scrollListener: EventCalendarScrollListener? = null
    internal var eventArrayList: ArrayList<Event> = ArrayList()
    private var _disallowIntercept = false

    // For xml layout
    internal var headerVisible = true
    internal var _calendarWeekVisible = false
    internal var currentDayBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_charcoal_color)
    internal var currentDayTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var countBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_charcoal_color)
    internal var countBackgroundTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var countVisible = true
    internal var eventItemAutomaticTextColor = true
    internal var eventItemTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var edgeToEdgeEnabled = false

    init {
        getContext().withStyledAttributes(attrs, R.styleable.EventCalendarView) {
            headerVisible =
                getBoolean(R.styleable.EventCalendarView_ecv_header_visible, headerVisible)
            _calendarWeekVisible = getBoolean(
                R.styleable.EventCalendarView_ecv_calendar_week_visible, _calendarWeekVisible
            )
            disallowIntercept =
                getBoolean(R.styleable.EventCalendarView_ecv_disallow_intercept, disallowIntercept)
            currentDayBackgroundTintColor = getColor(
                (R.styleable.EventCalendarView_ecv_current_day_background_tint_color),
                currentDayBackgroundTintColor
            )
            currentDayTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_current_day_text_color), currentDayTextColor
            )
            countBackgroundTintColor = getColor(
                (R.styleable.EventCalendarView_ecv_count_background_tint_color),
                countBackgroundTintColor
            )
            countBackgroundTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_count_background_text_color),
                countBackgroundTextColor
            )
            countVisible = getBoolean(R.styleable.EventCalendarView_ecv_count_visible, countVisible)
            eventItemAutomaticTextColor = getBoolean(
                R.styleable.EventCalendarView_ecv_event_item_automatic_text_color,
                eventItemAutomaticTextColor
            )
            eventItemTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_event_item_text_color), eventItemTextColor
            )

            edgeToEdgeEnabled = getBoolean(
                R.styleable.EventCalendarView_ecv_edge_to_edge_enabled,
                edgeToEdgeEnabled
            )
        }

        addView(binding.root)

        with(binding) {
            val appCompatActivity = getContext().getActivity() as? AppCompatActivity
            if (appCompatActivity != null) {
                with(eventCalendarRecyclerView) {
                    setScrollingTouchSlop(RecyclerView.TOUCH_SLOP_PAGING)
                    setHasFixedSize(true)
                    setItemViewCacheSize(1000)
                    adapter = InfiniteAdapter(this@EventCalendarView)
                    attachSnapHelperWithListener(snapHelper = PagerSnapHelper(),
                        onSnapPositionChangeListener = object :
                            SnapOnScrollListener.OnSnapPositionChangeListener {
                            override fun onSnapPositionChange(position: Int) {
                                if (position != currentRecyclerViewPosition) {
                                    // We only invoke the scroll change event if the position is a new one
                                    scrollHelper(position)
                                }
                            }
                        })
                }

                val currentMonthPosition =
                    ((currentYear - sYear) * 12) + (currentMonth - sMonth) - (if (currentMonth >= eMonth && currentYear >= eYear) (currentYear - eYear) * 12 + (currentMonth - eMonth) else 0)
                eventCalendarRecyclerView.scrollToPosition(currentMonthPosition)

                currentRecyclerViewPosition = currentMonthPosition
                currentYearAndMonthPair = getMonthNameAndYear(currentMonthPosition)
            }

            binding.root.post {
                // We need to make sure that we inform the user about the first scroll after
                // the initialization is done.
                scrollHelper(currentRecyclerViewPosition)
            }
        }
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
     * Set a [EventCalendarScrollListener] to the [EventCalendarView].
     * You can also insert null to remove the listener.
     * @param eventCalendarScrollListener [EventCalendarScrollListener]?
     */
    fun addOnCalendarScrollListener(eventCalendarScrollListener: EventCalendarScrollListener?) {
        scrollListener = eventCalendarScrollListener
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
            updateRecyclerView(false)
        }

    /**
     * Scrolls the calendar to the given month and year.
     * If the month and year is not in range, no action is taken.
     * @param month eg. 1 scrolls to January or 12 to December
     * @param year eg. 2023
     * @param smoothScroll Indicates whether scrolling should be smooth or immediate.
     * @param scrollToLastIfOutOfRange Indicates whether scrolling to the last existing position is executed or not.
     * Default is `false`
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun scrollTo(
        month: Int,
        year: Int,
        smoothScroll: Boolean = false,
        scrollToLastIfOutOfRange: Boolean = false,
    ) {
        /*
         * Checks if the given month and year are within the range of the calendar.
         * If yes, calculates the position of the month on the horizontal axis.
         * If the month/year is not in range, no action is taken accept `scrollToLastIfOutOfRange` is
         * set to `true`.
         */
        val booleanIntPair1 = monthInRange(month.minus(1), year)
        val scrollPosition = if (booleanIntPair1.first) {
            abs(booleanIntPair1.second)
        } else {
            if (scrollToLastIfOutOfRange) {
                binding.eventCalendarRecyclerView.adapter?.itemCount?.minus(1)
            } else {
                null
            }
        }

        // Only scroll if the position is not null
        scrollPosition?.let { position ->
            if (position == currentRecyclerViewPosition) {
                return
            }
            if (!smoothScroll) {
                // We use here the same logic as we know it from ViewPager2 class
                // We jump instant to the left or right item side which we want and then
                // perform the final scroll as animation.
                binding.eventCalendarRecyclerView.scrollToPosition(if (position > currentRecyclerViewPosition) position - 1 else position + 1)
            }
            binding.eventCalendarRecyclerView.smoothScrollTo(position)
        }
    }

    /**
     * Scrolls the calendar to the current month of the current year.
     * If the month and year is not in range, no action is taken.
     * @param smoothScroll Indicates whether scrolling should be smooth or immediate.
     * @param scrollToLastIfOutOfRange Indicates whether scrolling to the last existing position is executed or not.
     * Default is `false`
     */
    fun scrollToCurrentMonth(
        smoothScroll: Boolean = false,
        scrollToLastIfOutOfRange: Boolean = false,
    ) {
        scrollTo(currentMonth.plus(1), currentYear, smoothScroll, scrollToLastIfOutOfRange)
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun scrollHelper(position: Int) {
        currentRecyclerViewPosition = position
        currentYearAndMonthPair = getMonthNameAndYear(position)
        scrollListener?.onScrolled(
            month = currentYearAndMonthPair.second.plus(1), year = currentYearAndMonthPair.first
        )
    }

    /**
     * Set the start/end month and year to the [EventCalendarView].
     * Use eg. 4 as "startMonth" to set the start month to april.
     * @param startMonth Int. Default is `JANUARY`
     * @param startYear Int. Default is `2020`
     * @param endMonth Int. Default is `DECEMBER`
     * @param endYear Int. Default is `current year + 1`
     * @param forceRecreate Boolean. Default is `false`
     */
    fun setMonthAndYear(
        startMonth: Int, startYear: Int, endMonth: Int, endYear: Int, forceRecreate: Boolean = false
    ) {
        val isStartMonthYearChanged = sMonth != startMonth - 1 || sYear != startYear
        val isEndMonthYearChanged = eMonth != endMonth - 1 || eYear != endYear

        if (forceRecreate || isStartMonthYearChanged || isEndMonthYearChanged) {
            //val numberOfNewMonths = ((eYear - startYear) * 12 + (eMonth - startMonth)).plus(1)

            sMonth = startMonth - 1
            sYear = startYear
            eMonth = endMonth - 1
            eYear = endYear

            updateRecyclerView(true)
        }
    }

    /**
     * @return The current start month
     */
    val startMonth: Int
        get() {
            return sMonth
        }

    /**
     * @return The current start year
     */
    val startYear: Int
        get() {
            return sYear
        }

    /**
     * @return The current end month
     */
    val endMonth: Int
        get() {
            return eMonth
        }

    /**
     * @return The current end year
     */
    val endYear: Int
        get() {
            return eYear
        }

    /**
     * Set this to true if you want to display the [EventCalendarView] inside on a [ViewPager2] object.
     * Then the [EventCalendarView] will consume the scroll event and not the [ViewPager2].
     *
     * You can also set this value inside your xml layout:
     * ```
     * app:ecv_disallow_intercept="true"
     * ```
     * Default is `false`
     */
    @Suppress("KDocUnresolvedReference")
    var disallowIntercept: Boolean
        get() = _disallowIntercept
        set(value) {
            _disallowIntercept = value
        }

    /**
     * Set this to true if you want to display the calendar week on each week row.
     *
     * You can also set this value inside your xml layout:
     * ```
     * app:ecv_calendar_week_visible="true"
     * ```
     * Default is `false`
     */
    var calendarWeekVisible: Boolean
        get() = _calendarWeekVisible
        set(value) {
            _calendarWeekVisible = value
            updateRecyclerView(false)
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        binding.eventCalendarRecyclerView.adapter?.notifyDataSetChanged()
    }

    /**
     * Request all parents to relinquish the touch events
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(/* disallowIntercept = */ disallowIntercept)
        return super.dispatchTouchEvent(ev)
    }

    override fun onSaveInstanceState(): Parcelable? {
        // Save the last state
        InstanceState().saveInstanceState(
            id = id, stateModel = InstanceState.StateModel(
                disallowIntercept = disallowIntercept,
                calendarWeekVisible = _calendarWeekVisible,
                currentRecyclerViewPosition = currentRecyclerViewPosition,
                startMonth = sMonth,
                startYear = sYear,
                endMonth = eMonth,
                endYear = eYear,
                currentYearAndMonthPair = currentYearAndMonthPair,
                events = events
            )
        )

        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        // Restore to the last state
        InstanceState().restoreInstanceState(id)?.let { sharedPreferencesModel ->
            disallowIntercept = sharedPreferencesModel.disallowIntercept
            _calendarWeekVisible = sharedPreferencesModel.calendarWeekVisible
            currentRecyclerViewPosition = sharedPreferencesModel.currentRecyclerViewPosition
            sMonth = sharedPreferencesModel.startMonth
            sYear = sharedPreferencesModel.startYear
            eMonth = sharedPreferencesModel.endMonth
            eYear = sharedPreferencesModel.endYear
            currentYearAndMonthPair = sharedPreferencesModel.currentYearAndMonthPair
            events = sharedPreferencesModel.events
        }
        InstanceState().removeArrayListEventById(id)

        binding.eventCalendarRecyclerView.scrollToPosition(currentRecyclerViewPosition)
        super.onRestoreInstanceState(state)
    }

    /**
     * Internal method.
     */
    @SuppressLint("NotifyDataSetChanged")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun updateRecyclerView(dateRangeChanged: Boolean) {
        with(binding) {
            eventCalendarRecyclerView.adapter = eventCalendarRecyclerView.adapter
            if (dateRangeChanged) {
                val validPosition = getValidRecyclerViewPosition()
                eventCalendarRecyclerView.scrollToPosition(validPosition)
                currentRecyclerViewPosition = validPosition
            } else {
                eventCalendarRecyclerView.scrollToPosition(currentRecyclerViewPosition)
            }
        }
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun getValidRecyclerViewPosition(): Int {
        val booleanIntPair =
            monthInRange(currentYearAndMonthPair.second, currentYearAndMonthPair.first)

        // First we check if old recyclerview position is still valid for new date range
        val value = if (booleanIntPair.first) {
            // Still valid
            abs(booleanIntPair.second)
        } else {
            // It's time to check if the current month and year is in the new date range
            val booleanIntPair1 = monthInRange(currentMonth, currentYear)
            if (booleanIntPair1.first) {
                // It's valid
                abs(booleanIntPair1.second)
            } else {
                0
            }
        }
        return value
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun monthInRange(m: Int, y: Int): Pair<Boolean, Int> {
        val monthDiffStart = (sYear - y) * 12 + (sMonth - m)

        val monthInRange =
            y in sYear..eYear && (y != sYear || m >= sMonth) && (y != eYear || m <= eMonth)

        return Pair(first = monthInRange, second = monthDiffStart)
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun getMonthNameAndYear(
        position: Int,
    ): Pair<Int, Int> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return getYearAndMonthFromPosition(position)
        }

        val adjustedPosition = position + (sYear * 12) + sMonth
        val year = adjustedPosition / 12
        val month = adjustedPosition % 12

        val calendar = Calendar.getInstance()
        if (year == eYear && month > eMonth) {
            calendar.set(eYear, eMonth, 1)
        } else {
            calendar.set(sYear, month, 1)
        }
        return Pair(first = year, second = calendar.get(Calendar.MONTH))
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getYearAndMonthFromPosition(position: Int): Pair<Int, Int> {
        val adjustedPosition = position + (startYear * 12) + startMonth
        val year = adjustedPosition / 12
        val month = adjustedPosition % 12

        val yearMonth =
            YearMonth.of(if (year == endYear && month > endMonth) endYear else startYear, month + 1)

        return Pair(first = year, second = yearMonth.monthValue - 1)
    }

}