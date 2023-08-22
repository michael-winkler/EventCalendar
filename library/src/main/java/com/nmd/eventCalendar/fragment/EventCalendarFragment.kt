package com.nmd.eventCalendar.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.updateLayoutParams
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.adapter.EventsAdapter
import com.nmd.eventCalendar.builder.EventCalendarBuilder
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList

class EventCalendarFragment : Fragment() {
    private lateinit var rootView: View
    private lateinit var eventCalendarViewMonthYearTextView: MaterialTextView
    private lateinit var eventCalendarViewMonthYearImageViewLeft: ImageView
    private lateinit var eventCalendarViewMonthYearImageViewRight: ImageView
    private lateinit var eventCalendarViewMonthYearHeader: RelativeLayout
    private var eventCalendarViewNestedScrollView: NestedScrollView? = null
    var eventCalendarView: EventCalendarView? = null
    private var oldEventCalendarViewNestedScrollViewScrollPositionY: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {

        val orientation = resources.configuration.orientation
        val layoutId = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            R.layout.ecv_event_calendar_view
        } else {
            R.layout.ecv_event_calendar_view_land
        }
        rootView = inflater.inflate(layoutId, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        eventCalendarViewMonthYearTextView =
            rootView.findViewById(R.id.eventCalendarViewMonthYearTextView)
        eventCalendarViewMonthYearImageViewLeft =
            rootView.findViewById(R.id.eventCalendarViewMonthYearImageViewLeft)
        eventCalendarViewMonthYearImageViewRight =
            rootView.findViewById(R.id.eventCalendarViewMonthYearImageViewRight)
        eventCalendarViewMonthYearHeader =
            rootView.findViewById(R.id.eventCalendarViewMonthYearHeader)

        eventCalendarViewNestedScrollView =
            rootView.findViewById(R.id.eventCalendarViewNestedScrollView)
        eventCalendarViewNestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
            oldEventCalendarViewNestedScrollViewScrollPositionY = oldScrollY
        })

        eventCalendarViewNestedScrollView?.scrollY =
            oldEventCalendarViewNestedScrollViewScrollPositionY

        arguments?.let { bundle ->
            val month = bundle.getInt(EventCalendarBuilder.BUILDER_MONTH)
            val year = bundle.getInt(EventCalendarBuilder.BUILDER_YEAR)

            val monthYearText = month.getMonthName(requireContext()) + " " + year
            eventCalendarViewMonthYearTextView.text = monthYearText

            eventCalendarViewMonthYearImageViewLeft.setOnClickListener {
                val scrollTo = currentItem.minus(1)
                eventCalendarView?.eventCalendarViewPager2?.setCurrentItem(scrollTo, true)
            }
            eventCalendarViewMonthYearImageViewRight.setOnClickListener {
                val scrollTo = currentItem.plus(1)
                eventCalendarView?.eventCalendarViewPager2?.setCurrentItem(scrollTo, true)
            }

            eventCalendarViewMonthYearHeader.visibility =
                if (eventCalendarView?.headerVisible == true) View.VISIBLE else View.GONE

            initTextViews(month.getDaysOfMonthAndGivenYear(year))
        }
    }

    private val currentItem: Int
        get() = eventCalendarView?.currentViewPager2Position ?: 0

    private val listOfRows: List<LinearLayout> by lazy {
        listOf(
            rootView.findViewById(R.id.eventCalendarViewRow1),
            rootView.findViewById(R.id.eventCalendarViewRow2),
            rootView.findViewById(R.id.eventCalendarViewRow3),
            rootView.findViewById(R.id.eventCalendarViewRow4),
            rootView.findViewById(R.id.eventCalendarViewRow5),
            rootView.findViewById(R.id.eventCalendarViewRow6)
        )
    }

    private val dayIds by lazy {
        intArrayOf(
            R.id.eventCalendarViewDay1,
            R.id.eventCalendarViewDay2,
            R.id.eventCalendarViewDay3,
            R.id.eventCalendarViewDay4,
            R.id.eventCalendarViewDay5,
            R.id.eventCalendarViewDay6,
            R.id.eventCalendarViewDay7
        )
    }

    private fun initTextViews(days: List<Day>) {
        val list = arrayListOf<MaterialCardView>()

        listOfRows.forEach { row ->
            dayIds.forEach { dayId ->
                list.add(row.findViewById(dayId))
            }
        }

        if (days.size != list.size) {
            throw RuntimeException("Day-List and MaterialCardView-List can not be different!")
        }

        styleTextViews(days, list)
    }

    private fun styleTextViews(days: List<Day>, list: List<MaterialCardView>) {
        days.forEachIndexed { index, day ->
            val materialCardView = list[index]

            if (resources.configuration.orientation != Configuration.ORIENTATION_PORTRAIT) {
                materialCardView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                    override fun onLayoutChange(
                        v: View?, left: Int, top: Int, right: Int, bottom: Int,
                        oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int,
                    ) {
                        if (v == materialCardView) {
                            val width = materialCardView.width
                            val height = materialCardView.height

                            if (width != 0 && height != 0) {
                                materialCardView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                                    this.height = width
                                }

                                materialCardView.removeOnLayoutChangeListener(this)
                            }
                        }
                    }
                })
            }

            val frameLayout: FrameLayout =
                materialCardView.findViewById(R.id.eventCalendarViewDayFrameLayout)
            frameLayout.setOnClickListener {
                eventCalendarView?.clickListener?.onClick(day)
            }

            val textView: MaterialTextView =
                materialCardView.findViewById(R.id.eventCalendarViewDayTextView)

            val eventList = day.dayEvents(eventCalendarView?.eventArrayList.orEmptyArrayList())
            val recyclerView: RecyclerView =
                materialCardView.findViewById(R.id.eventCalendarViewDayRecyclerView)

            if (eventCalendarView?.countVisible == true) {
                recyclerView.addItemDecoration(LastPossibleVisibleItemForUserDecoration(eventList))
                // Old code
                /*
                recyclerView.post {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition()

                    if (eventList.size.minus(1) != lastVisiblePosition || lastVisiblePosition == -1) {
                        val position = if (lastVisiblePosition == -1) 0 else lastVisiblePosition
                        val holderLastItem =
                            recyclerView.findViewHolderForAdapterPosition(position) as? EventsAdapter.AdapterViewHolder
                        val count = eventList.size.minus(lastVisiblePosition)
                        holderLastItem.markLastEvent(
                            count,
                            eventCalendarView?.countBackgroundTintColor,
                            eventCalendarView?.countBackgroundTextColor
                        )
                    }
                }
                 */
            }

            recyclerView.adapter = if (eventList.isEmpty()) {
                null
            } else {
                EventsAdapter(eventList, eventCalendarView)
            }

            textView.text = day.value

            if (day.isCurrentDay) {
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                eventCalendarView?.currentDayTextColor?.let {
                    textView.setTextColor(it)
                }
                textView.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ecv_circle)
                eventCalendarView?.currentDayBackgroundTintColor?.let {
                    ViewCompat.setBackgroundTintList(textView, ColorStateList.valueOf(it))
                }
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
                val materialCardView: MaterialCardView =
                    parent.findViewHolderForAdapterPosition(lastCompleteVisiblePosition)?.itemView as? MaterialCardView
                        ?: return
                val textView =
                    materialCardView.findViewById<MaterialTextView>(R.id.itemEventMaterialTextView)
                        ?: return
                textView.text = "+${count.plus(1)}"

                textView.setTextColor(
                    eventCalendarView?.countBackgroundTextColor ?: ContextCompat.getColor(
                        textView.context,
                        R.color.ecv_white
                    )
                )
                textView.setTypeface(
                    textView.typeface,
                    Typeface.BOLD
                )

                materialCardView.setCardBackgroundColor(
                    eventCalendarView?.countBackgroundTintColor ?: ContextCompat.getColor(
                        materialCardView.context,
                        R.color.ecv_charcoal_color
                    )
                )

                materialCardView.invalidate()
                materialCardView.requestLayout()
            }

            for (i in lastCompleteVisiblePosition + 1..eventList.lastIndex) {
                val view = parent.findViewHolderForAdapterPosition(i)?.itemView ?: continue
                view.visibility = View.GONE
            }
        }
    }

}