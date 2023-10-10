package com.nmd.eventCalendar

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.adapter.EventsAdapter
import com.nmd.eventCalendar.databinding.EcvEventCalendarSingleWeekViewBinding
import com.nmd.eventCalendar.databinding.EcvTextviewCircleBinding
import com.nmd.eventCalendar.`interface`.EventCalendarDayClickListener
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.getCurrentWeekNumber
import com.nmd.eventCalendar.utils.Utils.Companion.getCurrentYear
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysForCurrentWeek
import com.nmd.eventCalendar.utils.Utils.Companion.getRealContext
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList

class EventCalendarSingleWeekView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {
    private val binding =
        EcvEventCalendarSingleWeekViewBinding.inflate(LayoutInflater.from(context))

    private var clickListener: EventCalendarDayClickListener? = null
    private var eventArrayList: ArrayList<Event> = ArrayList()

    // For xml layout
    private var headerVisible = true
    private var currentDayBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_charcoal_color)
    private var currentDayTextColor =
        ContextCompat.getColor(getContext(), R.color.ecv_white)
    internal var countBackgroundTintColor =
        ContextCompat.getColor(getContext(), R.color.ecv_charcoal_color)
    internal var countBackgroundTextColor =
        ContextCompat.getColor(getContext(), R.color.ecv_white)
    private var countVisible = true
    private var eventItemAutomaticTextColor = true
    private var eventItemTextColor = ContextCompat.getColor(getContext(), R.color.ecv_white)

    init {
        getContext().withStyledAttributes(attrs, R.styleable.EventCalendarView) {
            headerVisible =
                getBoolean(R.styleable.EventCalendarView_ecv_header_visible, headerVisible)
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
            eventItemAutomaticTextColor =
                getBoolean(
                    R.styleable.EventCalendarView_ecv_event_item_automatic_text_color,
                    eventItemAutomaticTextColor
                )
            eventItemTextColor = getColor(
                (R.styleable.EventCalendarView_ecv_event_item_text_color),
                eventItemTextColor
            )
        }

        addView(binding.root)
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
     * Internal method.
     */
    @SuppressLint("NotifyDataSetChanged")
    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    private fun updateLayout() {
        with(binding) {
            val monthYearText = "" + getCurrentWeekNumber() + " • " + getCurrentYear()
            eventCalendarSingleWeekViewMonthYearTextView1.text = monthYearText

            eventCalendarSingleWeekViewMonthYearHeader.post {
                eventCalendarSingleWeekViewMonthYearHeader.visibility =
                    if (headerVisible) View.VISIBLE else View.GONE
            }

            initTextViews(getDaysForCurrentWeek())
        }
    }

    private fun initTextViews(days: List<Day>) {
        val bindingArrayList = arrayListOf(
            binding.eventCalendarSingleWeekViewDay1,
            binding.eventCalendarSingleWeekViewDay2,
            binding.eventCalendarSingleWeekViewDay3,
            binding.eventCalendarSingleWeekViewDay4,
            binding.eventCalendarSingleWeekViewDay5,
            binding.eventCalendarSingleWeekViewDay6,
            binding.eventCalendarSingleWeekViewDay7
        )

        if (days.size != bindingArrayList.size) {
            throw RuntimeException("Day-List and Bindings-List can not be different!")
        }

        val eventArrayList1 = ArrayList<Event>()
        days.forEach { day ->
            eventArrayList1.addAll(eventArrayList.filter { it.date == day.date })
        }

        styleTextViews(days, bindingArrayList, ArrayList(eventArrayList1))
    }

    private fun styleTextViews(
        days: List<Day>,
        list: List<EcvTextviewCircleBinding>,
        eventsList: ArrayList<Event>,
    ) {
        days.forEachIndexed { index, day ->
            val materialCardView = list[index]

            materialCardView.eventCalendarViewDayFrameLayout.setOnClickListener {
                clickListener?.onClick(day)
            }

            val textView: MaterialTextView = materialCardView.eventCalendarViewDayTextView

            val eventList = day.dayEvents(eventsList.orEmptyArrayList())
            val recyclerView: RecyclerView = materialCardView.eventCalendarViewDayRecyclerView

            if (countVisible) {
                recyclerView.addItemDecoration(
                    LastPossibleVisibleItemForUserDecoration(
                        eventList
                    )
                )
            }

            if (eventList.isNotEmpty()) {
                recyclerView.adapter = EventsAdapter(
                    eventList,
                    eventItemAutomaticTextColor,
                    eventItemTextColor
                )
            }

            textView.text = day.value

            if (day.isCurrentDay) {
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                textView.setTextColor(currentDayTextColor)

                context.getRealContext()?.let {
                    textView.background =
                        ContextCompat.getDrawable(it, R.drawable.ecv_circle)
                }

                ViewCompat.setBackgroundTintList(
                    textView,
                    ColorStateList.valueOf(currentDayBackgroundTintColor)
                )
            }

            if (day.isCurrentMonth) {
                textView.setTypeface(textView.typeface, Typeface.BOLD)
            } else {
                textView.setTypeface(textView.typeface, Typeface.ITALIC)
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
                val materialCardView: MaterialCardView? =
                    parent.findViewHolderForAdapterPosition(lastCompleteVisiblePosition)?.itemView as? MaterialCardView
                val textView =
                    materialCardView?.findViewById<MaterialTextView>(R.id.itemEventMaterialTextView)

                textView?.text = "+${count.plus(1)}"
                textView?.setTextColor(countBackgroundTextColor)
                textView?.setTypeface(textView.typeface, Typeface.BOLD)

                materialCardView?.setCardBackgroundColor(countBackgroundTintColor)
            }

            for (i in lastCompleteVisiblePosition + 1..eventList.lastIndex) {
                val view = parent.findViewHolderForAdapterPosition(i)?.itemView ?: continue
                view.visibility = View.GONE
            }
        }
    }

}