package com.nmd.eventCalendar.fragment

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.nmd.eventCalendar.databinding.EcvIncludeRowsBinding
import com.nmd.eventCalendar.databinding.EcvTextviewCircleBinding
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.getRealContext
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList
import com.nmd.eventCalendar.utils.Utils.Companion.orTrue

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

        binding.eventCalendarViewNestedScrollView?.post {
            binding.eventCalendarViewNestedScrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, _, _, oldScrollY ->
                oldEventCalendarViewNestedScrollViewScrollPositionY = oldScrollY
            })
        }

        binding.eventCalendarViewNestedScrollView?.post {
            binding.eventCalendarViewNestedScrollView?.scrollY =
                oldEventCalendarViewNestedScrollViewScrollPositionY
        }

        arguments?.let { bundle ->
            val month = bundle.getInt(EventCalendarBuilder.BUILDER_MONTH)
            val year = bundle.getInt(EventCalendarBuilder.BUILDER_YEAR)

            val monthYearText = month.getMonthName(requireContext()) + " " + year
            binding.eventCalendarViewMonthYearTextView1?.text = monthYearText
            binding.eventCalendarViewMonthYearTextView2?.text = monthYearText

            binding.eventCalendarViewMonthYearImageViewLeft.setOnClickListener {
                eventCalendarView?.binding?.eventCalendarViewPager2?.setCurrentItem(
                    currentItem - 1,
                    true
                )
            }

            binding.eventCalendarViewMonthYearImageViewRight.setOnClickListener {
                eventCalendarView?.binding?.eventCalendarViewPager2?.setCurrentItem(
                    currentItem + 1,
                    true
                )
            }

            binding.eventCalendarViewMonthYearHeader.post {
                binding.eventCalendarViewMonthYearHeader.visibility =
                    if (eventCalendarView?.headerVisible == true) View.VISIBLE else View.GONE
            }

            initTextViews(month.getDaysOfMonthAndGivenYear(year))
        }
    }

    private val currentItem: Int
        get() = eventCalendarView?.currentViewPager2Position ?: 0

    private fun initTextViews(days: List<Day>) {

        val listOfRows: List<EcvIncludeRowsBinding> = listOf(
            binding.eventCalendarViewRow1,
            binding.eventCalendarViewRow2,
            binding.eventCalendarViewRow3,
            binding.eventCalendarViewRow4,
            binding.eventCalendarViewRow5,
            binding.eventCalendarViewRow6
        )

        val bindingArrayList = ArrayList<EcvTextviewCircleBinding>()
        listOfRows.forEach {
            bindingArrayList.add(it.eventCalendarViewDay1)
            bindingArrayList.add(it.eventCalendarViewDay2)
            bindingArrayList.add(it.eventCalendarViewDay3)
            bindingArrayList.add(it.eventCalendarViewDay4)
            bindingArrayList.add(it.eventCalendarViewDay5)
            bindingArrayList.add(it.eventCalendarViewDay6)
            bindingArrayList.add(it.eventCalendarViewDay7)
        }

        if (days.size != bindingArrayList.size) {
            throw RuntimeException("Day-List and Bindings-List can not be different!")
        }

        styleTextViews(days, bindingArrayList)
    }

    private fun styleTextViews(days: List<Day>, list: List<EcvTextviewCircleBinding>) {
        days.forEachIndexed { index, day ->
            val materialCardView = list[index]

            materialCardView.eventCalendarViewDayFrameLayout.setOnClickListener {
                eventCalendarView?.clickListener?.onClick(day)
            }

            val textView: MaterialTextView = materialCardView.eventCalendarViewDayTextView

            val eventList = day.dayEvents(eventCalendarView?.eventArrayList.orEmptyArrayList())
            val recyclerView: RecyclerView = materialCardView.eventCalendarViewDayRecyclerView

            if (eventCalendarView?.countVisible == true) {
                recyclerView.addItemDecoration(
                    LastPossibleVisibleItemForUserDecoration(
                        eventList
                    )
                )
            }

            if (eventList.isNotEmpty()) {
                recyclerView.adapter = EventsAdapter(
                    list = eventList,
                    eventItemAutomaticTextColor = eventCalendarView?.eventItemAutomaticTextColor.orTrue(),
                    eventItemTextColor = eventCalendarView?.eventItemTextColor
                        ?: ContextCompat.getColor(requireContext(), R.color.ecv_white)
                )
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
                val materialCardView: MaterialCardView? =
                    parent.findViewHolderForAdapterPosition(lastCompleteVisiblePosition)?.itemView as? MaterialCardView
                val textView =
                    materialCardView?.findViewById<MaterialTextView>(R.id.itemEventMaterialTextView)

                textView?.text = "+${count.plus(1)}"

                textView?.setTextColor(
                    eventCalendarView?.countBackgroundTextColor ?: ContextCompat.getColor(
                        textView.context,
                        R.color.ecv_white
                    )
                )

                textView?.setTypeface(textView.typeface, Typeface.BOLD)

                materialCardView?.setCardBackgroundColor(
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