package com.nmd.eventCalendar.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.R
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
import com.nmd.eventCalendar.utils.Utils.Companion.smoothScrollTo
import java.util.Calendar

class InfiniteAdapter(private val eventCalendarView: EventCalendarView) :
    RecyclerView.Adapter<InfiniteAdapter.AdapterViewHolder>() {

    inner class AdapterViewHolder(val binding: EcvEventCalendarViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val yearAdapterViewHolder = Calendar.getInstance().get(Calendar.YEAR)

        fun ecvTextviewCircleBindings(): ArrayList<EcvTextviewCircleBinding> {
            val listOfRows: List<EcvIncludeRowsBinding> = listOf(
                binding.eventCalendarViewRow1,
                binding.eventCalendarViewRow2,
                binding.eventCalendarViewRow3,
                binding.eventCalendarViewRow4,
                binding.eventCalendarViewRow5,
                binding.eventCalendarViewRow6
            )

            val bindingArrayList = ArrayList<EcvTextviewCircleBinding>()
            for (row in listOfRows) {
                bindingArrayList.add(row.eventCalendarViewDay1)
                bindingArrayList.add(row.eventCalendarViewDay2)
                bindingArrayList.add(row.eventCalendarViewDay3)
                bindingArrayList.add(row.eventCalendarViewDay4)
                bindingArrayList.add(row.eventCalendarViewDay5)
                bindingArrayList.add(row.eventCalendarViewDay6)
                bindingArrayList.add(row.eventCalendarViewDay7)
            }
            return bindingArrayList
        }

        init {
            binding.eventCalendarViewMonthYearHeader.visibility =
                if (eventCalendarView.headerVisible) View.VISIBLE else View.GONE

            binding.eventCalendarViewMonthYearImageViewLeft.setOnClickListener {
                eventCalendarView.binding.eventCalendarRecyclerView.smoothScrollTo(
                    currentItem - 1
                )
            }

            binding.eventCalendarViewMonthYearImageViewRight.setOnClickListener {
                eventCalendarView.binding.eventCalendarRecyclerView.smoothScrollTo(
                    currentItem + 1
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(
            EcvEventCalendarViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        with(holder.binding) {
            val item = holder.bindingAdapterPosition.calculate()
            val month = item.get(0)
            val year = item.get(1)

            val monthName = month.getMonthName(root.context)
            val monthYearText =
                if (holder.yearAdapterViewHolder == year) monthName else "$monthName $year"
            eventCalendarViewMonthYearTextView1?.text = monthYearText
            eventCalendarViewMonthYearTextView2?.text = monthYearText

            styleTextViews(
                month.getDaysOfMonthAndGivenYear(year),
                holder.ecvTextviewCircleBindings()
            )
        }
    }

    override fun getItemCount(): Int = getMonthCount()

    private val currentItem: Int
        get() = eventCalendarView.currentRecyclerViewPosition

    private fun getMonthCount(): Int {
        val diffYear = eventCalendarView.eYear - eventCalendarView.sYear
        val diffMonth = eventCalendarView.eMonth - eventCalendarView.sMonth

        val diffTotal = diffYear * 12 + diffMonth + 1
        return maxOf(0, diffTotal)
    }

    private fun Int.calculate(): SparseIntArray {
        val year = eventCalendarView.sYear + this / 12
        val month = (this % 12 + eventCalendarView.sMonth) % 12
        return SparseIntArray().apply {
            put(0, month)
            put(1, year)
        }
    }

    private fun styleTextViews(days: List<Day>, list: List<EcvTextviewCircleBinding>) {
        for ((index, day) in days.withIndex()) {
            val dayItemLayout = list[index]

            dayItemLayout.eventCalendarViewDayFrameLayout.setOnClickListener {
                eventCalendarView.clickListener?.onClick(day)
            }

            val textView: MaterialTextView = dayItemLayout.eventCalendarViewDayTextView

            val eventList = day.dayEvents(eventCalendarView.eventArrayList.orEmptyArrayList())
            val recyclerView: RecyclerView = dayItemLayout.eventCalendarViewDayRecyclerView
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

                if (eventList.isNotEmpty()) {
                    if (eventCalendarView.countVisible) {
                        addItemDecoration(
                            LastPossibleVisibleItemForUserDecoration(
                                eventList
                            )
                        )
                    }

                    adapter = EventsAdapter(
                        list = eventList,
                        eventItemAutomaticTextColor = eventCalendarView.eventItemAutomaticTextColor.orTrue(),
                        eventItemTextColor = eventCalendarView.eventItemTextColor
                    )
                }
            }

            with(textView) {
                text = day.value

                if (day.isCurrentDay) {
                    setTextColor(eventCalendarView.currentDayTextColor)

                    context.getRealContext()?.let {
                        background =
                            ContextCompat.getDrawable(it, R.drawable.ecv_circle)
                    }

                    ViewCompat.setBackgroundTintList(
                        this,
                        ColorStateList.valueOf(eventCalendarView.currentDayBackgroundTintColor)
                    )
                }

                if (day.isCurrentMonth || day.isCurrentDay) {
                    setTypeface(typeface, Typeface.BOLD)
                } else {
                    setTypeface(typeface, Typeface.ITALIC)
                }
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
                    textView.setTextColor(eventCalendarView.countBackgroundTextColor)
                    textView.setTypeface(textView.typeface, Typeface.BOLD)

                    ViewCompat.setBackgroundTintList(
                        textView,
                        ColorStateList.valueOf(eventCalendarView.countBackgroundTintColor)
                    )
                }
            }

            for (i in lastCompleteVisiblePosition + 1..eventList.lastIndex) {
                val view = parent.findViewHolderForAdapterPosition(i)?.itemView ?: continue
                view.visibility = View.GONE
            }
        }
    }

}