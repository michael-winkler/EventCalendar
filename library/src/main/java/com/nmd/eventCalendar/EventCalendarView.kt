package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.annotation.RestrictTo
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.adapter.InfiniteViewPagerAdapter
import com.nmd.eventCalendar.instanceState.SavedState
import com.nmd.eventCalendar.`interface`.EventCalendarDayClickListener
import com.nmd.eventCalendar.`interface`.EventCalendarScrollListener
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.disableItemAnimation
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import java.util.*
import kotlin.math.abs

@Suppress("unused")
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
 *     app:ecv_header_visible="true" />
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
 * binding.eventCalendarView.events = ArrayList<Event>().apply {
 *     add(Event(date = "15.04.2023", name = "Vacation", backgroundHexColor = "#4badeb"))
 *     add(Event(date = "16.04.2023", name = "Home office", backgroundHexColor = "#e012ad"))
 *     add(Event(date = "17.04.2023", name = "Meeting", backgroundHexColor = "#e07912"))
 *     add(Event(date = "18.04.2023", name = "Vacation", backgroundHexColor = "#4badeb", data = "Let's go!"))
 *     }
 * ```
 * The date format have to be in format "dd.MM.yyyy".
 * For more details about how a event model should be
 * @see events()
 *
 */
class EventCalendarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    internal var currentViewPager2Position = 0
    internal var eventCalendarViewPager2: ViewPager2? = null
    private val currentCalendar = Calendar.getInstance()
    private val currentYear = currentCalendar.get(Calendar.YEAR)
    private val currentMonth = currentCalendar.get(Calendar.MONTH)

    private lateinit var currentMonthAndYearTriple: Triple<String, Int, Int>

    internal var sMonth = Calendar.JANUARY
    internal var sYear = 2020

    internal var eMonth = Calendar.DECEMBER
    internal var eYear = currentYear.plus(1)

    internal var clickListener: EventCalendarDayClickListener? = null
    internal var scrollListener: EventCalendarScrollListener? = null
    internal var eventArrayList: ArrayList<Event> = ArrayList()
    private var _disallowIntercept = false

    // For xml layout
    internal var headerVisible = true
    internal var currentDayBackgroundTintColor =
        ContextCompat.getColor(context, R.color.ecv_charcoal_color)
    internal var currentDayTextColor =
        ContextCompat.getColor(context, R.color.ecv_white)
    internal var countBackgroundTintColor =
        ContextCompat.getColor(context, R.color.ecv_charcoal_color)
    internal var countBackgroundTextColor =
        ContextCompat.getColor(context, R.color.ecv_white)
    internal var countVisible = true

    init {
        context.withStyledAttributes(attrs, R.styleable.EventCalendarView) {
            headerVisible =
                getBoolean(R.styleable.EventCalendarView_ecv_header_visible, headerVisible)
            disallowIntercept =
                getBoolean(R.styleable.EventCalendarView_ecv_disallow_intercept, disallowIntercept)
            currentDayBackgroundTintColor = getColor(
                (R.styleable.EventCalendarView_ecv_current_day_background_tint_color),
                currentDayBackgroundTintColor
            )
            currentDayTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_current_day_text_color),
                currentDayTextColor
            )
            countBackgroundTintColor = getColor(
                (R.styleable.EventCalendarView_ecv_count_background_tint_color),
                countBackgroundTintColor
            )
            countBackgroundTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_count_background_text_color),
                countBackgroundTextColor
            )
            countVisible =
                getBoolean(R.styleable.EventCalendarView_ecv_count_visible, countVisible)
        }

        if (isInEditMode) {
            val view: View? = LayoutInflater.from(context)
                .inflate(R.layout.ecv_android_studio_preview, this, false)
            view?.let {
                addView(it)
                androidStudioPreview(it)
            }
        } else {
            val view: View? =
                LayoutInflater.from(context).inflate(R.layout.ecv_event_calendar, this, false)
            view?.let {
                addView(it)
            }
            eventCalendarViewPager2 = view?.findViewById(R.id.eventCalendarViewPager2)

            val appCompatActivity = context as AppCompatActivity
            eventCalendarViewPager2?.adapter = InfiniteViewPagerAdapter(
                fragmentManager = appCompatActivity.supportFragmentManager,
                lifecycle = appCompatActivity.lifecycle,
                eventCalendarView = this,
            )

            val currentMonthPosition =
                ((currentYear - sYear) * 12) + (currentMonth - sMonth) - (if (currentMonth >= eMonth && currentYear >= eYear) (currentYear - eYear) * 12 + (currentMonth - eMonth) else 0)
            eventCalendarViewPager2?.setCurrentItem(currentMonthPosition, false)
            currentViewPager2Position = currentMonthPosition
            currentMonthAndYearTriple = getMonthNameAndYear(currentMonthPosition)

            eventCalendarViewPager2?.disableItemAnimation()
            eventCalendarViewPager2?.offscreenPageLimit = 1
            eventCalendarViewPager2?.isSaveEnabled = false

            eventCalendarViewPager2?.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int,
                ) {
                }

                override fun onPageSelected(position: Int) {
                    currentViewPager2Position = position
                    currentMonthAndYearTriple = getMonthNameAndYear(position)
                    scrollListener?.onScrolled(
                        month = currentMonthAndYearTriple.third.plus(1),
                        year = currentMonthAndYearTriple.second
                    )
                }

                override fun onPageScrollStateChanged(state: Int) {}
            })
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
     *
     * @return The current event list. Can not be null but empty list.
     */
    var events: ArrayList<Event>
        get() {
            return eventArrayList
        }
        set(events) {
            eventArrayList = events
            updateViewPager2(false)
        }

    /**
     * Scrolls the calendar to the given month and year.
     * If the month and year is not in range, no action is taken.
     * @param month eg. 1 scrolls to January or 12 to December
     * @param year eg. 2023
     * @param smoothScroll Indicates whether scrolling should be smooth or immediate.
     * Default is "false"
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun scrollTo(month: Int, year: Int, smoothScroll: Boolean = false) {
        /*
         * Checks if the given month and year are within the range of the calendar.
         * If yes, calculates the position of the month on the horizontal axis.
         * If the month/year is not in range, no action is taken.
         */
        val booleanIntPair1 = monthInRange(month.minus(1), year)
        val scrollPosition = if (booleanIntPair1.first) {
            abs(booleanIntPair1.second)
        } else {
            null
        }

        // Only scroll if the position is not null
        scrollPosition?.let {
            eventCalendarViewPager2?.setCurrentItem(/* item = */ it, /* smoothScroll = */
                smoothScroll
            )
        }
    }

    /**
     * Scrolls the calendar to the current month of the current year.
     * If the month and year is not in range, no action is taken.
     * @param smoothScroll Indicates whether scrolling should be smooth or immediate.
     * Default is "false"
     */
    fun scrollToCurrentMonth(smoothScroll: Boolean = false) {
        scrollTo(currentMonth.plus(1), currentYear, smoothScroll)
    }

    /**
     * Set the start/end month and year to the [EventCalendarView].
     * Use eg. 4 as "startMonth" to set the start month to april.
     * @param startMonth Int. Default is "JANUARY"
     * @param startYear Int. Default is "2020"
     * @param endMonth Int. Default is "DECEMBER"
     * @param endYear Int. Default is "current year + 1"
     */
    fun setMonthAndYear(
        startMonth: Int,
        startYear: Int,
        endMonth: Int,
        endYear: Int,
    ) {
        val isStartMonthYearChanged = sMonth != startMonth - 1 || sYear != startYear
        val isEndMonthYearChanged = eMonth != endMonth - 1 || eYear != endYear

        if (isStartMonthYearChanged || isEndMonthYearChanged) {
            //val numberOfNewMonths = ((eYear - startYear) * 12 + (eMonth - startMonth)).plus(1)

            sMonth = startMonth - 1
            sYear = startYear
            eMonth = endMonth - 1
            eYear = endYear

            updateViewPager2(true)
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
     * Default is "false"
     */
    @Suppress("MemberVisibilityCanBePrivate")
    var disallowIntercept: Boolean
        get() = _disallowIntercept
        set(value) {
            _disallowIntercept = value
        }

    @SuppressLint("NotifyDataSetChanged")
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        eventCalendarViewPager2?.adapter?.notifyDataSetChanged()
    }

    /**
     * Request all parents to relinquish the touch events
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        parent.requestDisallowInterceptTouchEvent(/* disallowIntercept = */ disallowIntercept)
        return super.dispatchTouchEvent(ev)
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val state = Bundle()
        state.putBoolean(SavedState.SAVED_STATE_DISALLOW_INTERCEPT, disallowIntercept)
        state.putInt(SavedState.SAVED_STATE_VIEWPAGER_POSITION, currentViewPager2Position)
        state.putInt(SavedState.SAVED_STATE_START_MONTH, sMonth)
        state.putInt(SavedState.SAVED_STATE_START_YEAR, sYear)
        state.putInt(SavedState.SAVED_STATE_END_MONTH, eMonth)
        state.putInt(SavedState.SAVED_STATE_END_YEAR, eYear)
        state.putParcelableArrayList(SavedState.SAVED_STATE_EVENTS, events)
        state.putSerializable(
            SavedState.SAVED_STATE_MONTH_AND_YEAR_TRIPLE, currentMonthAndYearTriple
        )
        return SavedState(superState, state)
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            val viewState = state.state
            super.onRestoreInstanceState(state.superState)
            disallowIntercept =
                viewState.getBoolean(SavedState.SAVED_STATE_DISALLOW_INTERCEPT, true)
            currentViewPager2Position =
                viewState.getInt(SavedState.SAVED_STATE_VIEWPAGER_POSITION, 0)
            sMonth = viewState.getInt(SavedState.SAVED_STATE_START_MONTH, Calendar.JANUARY)
            sYear = viewState.getInt(SavedState.SAVED_STATE_START_YEAR, 2020)
            eMonth = viewState.getInt(SavedState.SAVED_STATE_END_MONTH, Calendar.DECEMBER)
            eYear = viewState.getInt(
                SavedState.SAVED_STATE_END_YEAR, Calendar.getInstance().get(Calendar.YEAR).plus(1)
            )
            events = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val clazzName = ArrayList<Event>()::class.java
                viewState.getParcelable(SavedState.SAVED_STATE_EVENTS, clazzName) ?: ArrayList()
            } else {
                @Suppress("DEPRECATION") (viewState.getParcelableArrayList(SavedState.SAVED_STATE_EVENTS)
                    ?: ArrayList())
            }
            @Suppress("UNCHECKED_CAST") if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val clazzName = currentMonthAndYearTriple::class.java
                (viewState.getSerializable(
                    SavedState.SAVED_STATE_MONTH_AND_YEAR_TRIPLE, clazzName
                ))?.let {
                    currentMonthAndYearTriple = it
                }
            } else {
                @Suppress("DEPRECATION") (viewState.getSerializable(SavedState.SAVED_STATE_MONTH_AND_YEAR_TRIPLE) as Triple<String, Int, Int>?)?.let {
                    currentMonthAndYearTriple = it
                }
            }

            eventCalendarViewPager2?.setCurrentItem(currentViewPager2Position, false)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Internal method.
     */
    @SuppressLint("NotifyDataSetChanged")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun updateViewPager2(dateRangeChanged: Boolean) {
        eventCalendarViewPager2?.adapter = eventCalendarViewPager2?.adapter
        if (dateRangeChanged) {
            val validPosition = getValidViewPagerPosition()
            eventCalendarViewPager2?.setCurrentItem(validPosition, false)
            currentViewPager2Position = validPosition
        } else {
            eventCalendarViewPager2?.setCurrentItem(currentViewPager2Position, false)
        }
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun getValidViewPagerPosition(): Int {
        val booleanIntPair =
            monthInRange(currentMonthAndYearTriple.third, currentMonthAndYearTriple.second)
        booleanIntPair.first

        // First we check if old viewpager 2 position is still valid for new date range
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
    ): Triple<String, Int, Int> {
        val monthOffset = (sYear * 12) + sMonth
        val adjustedPosition = position + monthOffset
        val year = adjustedPosition / 12
        val month = adjustedPosition % 12

        val calendar = Calendar.getInstance()
        if (year == eYear && month > eMonth) {
            calendar.set(eYear, eMonth, 1)
        } else {
            calendar.set(sYear, month, 1)
        }

        val monthName = calendar.get(Calendar.MONTH).getMonthName(context)
        return Triple(first = monthName, second = year, third = calendar.get(Calendar.MONTH))
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun androidStudioPreview(view: View) {
        val linearLayout: LinearLayout = view.findViewById(R.id.ecv_android_studio_include)
        val relativeLayout: RelativeLayout? =
            linearLayout.findViewById(R.id.eventCalendarViewMonthYearHeader)
        val materialTextView: MaterialTextView? =
            linearLayout.findViewById(R.id.eventCalendarViewMonthYearTextView)

        // Display correct month and year inside header
        val monthYearText = Calendar.getInstance().get(Calendar.MONTH)
            .getMonthName(context) + " " + Calendar.getInstance().get(Calendar.YEAR)
        materialTextView?.text = monthYearText

        // Show or hide the header view
        relativeLayout?.visibility = if (headerVisible) View.VISIBLE else View.GONE
        initTextViews(
            Calendar.getInstance().get(Calendar.MONTH).getDaysOfMonthAndGivenYear(
                Calendar.getInstance().get(Calendar.YEAR)
            ), linearLayout
        )
    }

    /**
     * Internal method.
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun initTextViews(days: List<Day>, linearLayout: LinearLayout?) {
        linearLayout ?: return

        val dayIds = intArrayOf(
            R.id.eventCalendarViewDay1,
            R.id.eventCalendarViewDay2,
            R.id.eventCalendarViewDay3,
            R.id.eventCalendarViewDay4,
            R.id.eventCalendarViewDay5,
            R.id.eventCalendarViewDay6,
            R.id.eventCalendarViewDay7
        )

        val list = arrayListOf<MaterialCardView>()
        val listOfRows = arrayListOf<LinearLayout>(
            linearLayout.findViewById(R.id.eventCalendarViewRow1),
            linearLayout.findViewById(R.id.eventCalendarViewRow2),
            linearLayout.findViewById(R.id.eventCalendarViewRow3),
            linearLayout.findViewById(R.id.eventCalendarViewRow4),
            linearLayout.findViewById(R.id.eventCalendarViewRow5),
            linearLayout.findViewById(R.id.eventCalendarViewRow6)
        )

        listOfRows.forEach { row ->
            dayIds.forEach { dayId ->
                list.add(row.findViewById(dayId))
            }
        }

        if (days.size != list.size) {
            return
        }

        days.forEachIndexed { index, day ->
            val materialCardView = list[index]
            val textView: MaterialTextView =
                materialCardView.findViewById(R.id.eventCalendarViewDayTextView)
            textView.text = day.value
        }
    }

}