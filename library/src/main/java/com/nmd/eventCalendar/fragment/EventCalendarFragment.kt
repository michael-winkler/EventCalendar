package com.nmd.eventCalendar.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
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
import com.nmd.eventCalendar.databinding.EcvEventCalendarViewBinding
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.getRealContext
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList

class EventCalendarFragment : Fragment() {
    private lateinit var binding: EcvEventCalendarViewBinding
    var eventCalendarView: EventCalendarView? = null
    private var oldEventCalendarViewNestedScrollViewScrollPositionY: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = EcvEventCalendarViewBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.eventCalendarViewNestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
            oldEventCalendarViewNestedScrollViewScrollPositionY = oldScrollY
        })

        binding.eventCalendarViewNestedScrollView?.scrollY =
            oldEventCalendarViewNestedScrollViewScrollPositionY

        arguments?.let { bundle ->
            val month = bundle.getInt(EventCalendarBuilder.BUILDER_MONTH)
            val year = bundle.getInt(EventCalendarBuilder.BUILDER_YEAR)

            val monthYearText = month.getMonthName(requireContext()) + " " + year
            binding.eventCalendarViewMonthYearTextView1?.text = monthYearText
            binding.eventCalendarViewMonthYearTextView2?.text = monthYearText

            binding.eventCalendarViewMonthYearImageViewLeft.setOnClickListener {
                val scrollTo = currentItem.minus(1)
                eventCalendarView?.binding?.eventCalendarViewPager2?.setCurrentItem(scrollTo, true)
            }
            binding.eventCalendarViewMonthYearImageViewRight.setOnClickListener {
                val scrollTo = currentItem.plus(1)
                eventCalendarView?.binding?.eventCalendarViewPager2?.setCurrentItem(scrollTo, true)
            }

            binding.eventCalendarViewMonthYearHeader.visibility =
                if (eventCalendarView?.headerVisible == true) View.VISIBLE else View.GONE

            initTextViews(month.getDaysOfMonthAndGivenYear(year))
        }
    }

    private val currentItem: Int
        get() = eventCalendarView?.currentViewPager2Position ?: 0

    private val dayIds = intArrayOf(
        R.id.eventCalendarViewDay1,
        R.id.eventCalendarViewDay2,
        R.id.eventCalendarViewDay3,
        R.id.eventCalendarViewDay4,
        R.id.eventCalendarViewDay5,
        R.id.eventCalendarViewDay6,
        R.id.eventCalendarViewDay7
    )

    private fun initTextViews(days: List<Day>) {
        val listOfRows: List<LinearLayout> = listOf(
            binding.root.findViewById(R.id.eventCalendarViewRow1),
            binding.root.findViewById(R.id.eventCalendarViewRow2),
            binding.root.findViewById(R.id.eventCalendarViewRow3),
            binding.root.findViewById(R.id.eventCalendarViewRow4),
            binding.root.findViewById(R.id.eventCalendarViewRow5),
            binding.root.findViewById(R.id.eventCalendarViewRow6)
        )

        val list = listOfRows.flatMap { row ->
            dayIds.map { dayId ->
                row.findViewById<MaterialCardView>(dayId)
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
            }

            if (eventList.isNotEmpty()) {
                recyclerView.adapter = EventsAdapter(eventList, eventCalendarView)
            }

            textView.text = day.value

            if (day.isCurrentDay) {
                textView.setTypeface(textView.typeface, Typeface.BOLD)
                eventCalendarView?.currentDayTextColor?.let {
                    textView.setTextColor(it)
                }

                context.getRealContext()?.let {
                    textView.background =
                        ContextCompat.getDrawable(it, R.drawable.ecv_circle)
                }

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
            }

            for (i in lastCompleteVisiblePosition + 1..eventList.lastIndex) {
                val view = parent.findViewHolderForAdapterPosition(i)?.itemView ?: continue
                view.visibility = View.GONE
            }
        }
    }

}