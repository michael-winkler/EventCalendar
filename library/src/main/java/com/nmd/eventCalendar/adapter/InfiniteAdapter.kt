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
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.databinding.EcvEventCalendarViewBinding
import com.nmd.eventCalendar.databinding.EcvIncludeRowsBinding
import com.nmd.eventCalendar.databinding.EcvTextviewCircleBinding
import com.nmd.eventCalendar.databinding.EcvTextviewCwBinding
import com.nmd.eventCalendar.model.Day
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.convertStringToCalendarWeek
import com.nmd.eventCalendar.utils.Utils.Companion.dayEvents
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.getRealContext
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList
import com.nmd.eventCalendar.utils.Utils.Companion.orTrue
import com.nmd.eventCalendar.utils.Utils.Companion.smoothScrollTo
import java.util.Calendar

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class InfiniteAdapter(private val eventCalendarView: EventCalendarView) :
    RecyclerView.Adapter<InfiniteAdapter.AdapterViewHolder>() {

    inner class AdapterViewHolder(val binding: EcvEventCalendarViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val yearAdapterViewHolder = Calendar.getInstance().get(Calendar.YEAR)

        private val listOfRows: List<EcvIncludeRowsBinding> = listOf(
            binding.eventCalendarViewRow1,
            binding.eventCalendarViewRow2,
            binding.eventCalendarViewRow3,
            binding.eventCalendarViewRow4,
            binding.eventCalendarViewRow5,
            binding.eventCalendarViewRow6
        )

        fun ecvTextviewCircleBindings(): ArrayList<EcvTextviewCircleBinding> {
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

        fun ecvTextviewCwBinding(): ArrayList<EcvTextviewCwBinding> {
            val bindingArrayList = ArrayList<EcvTextviewCwBinding>()
            for (row in listOfRows) {
                bindingArrayList.add(row.eventCalendarViewCalendarWeek)
            }
            return bindingArrayList
        }

        init {
            with(binding) {
                eventCalendarViewMonthYearHeader.isVisible = eventCalendarView.headerVisible

                listOf(
                    eventCalendarViewHeaderKw,
                    eventCalendarViewRow1.eventCalendarViewCalendarWeek.root,
                    eventCalendarViewRow2.eventCalendarViewCalendarWeek.root,
                    eventCalendarViewRow3.eventCalendarViewCalendarWeek.root,
                    eventCalendarViewRow4.eventCalendarViewCalendarWeek.root,
                    eventCalendarViewRow5.eventCalendarViewCalendarWeek.root,
                    eventCalendarViewRow6.eventCalendarViewCalendarWeek.root
                ).forEach {
                    it.isVisible = eventCalendarView.calendarWeekVisible
                }

                if (eventCalendarView.edgeToEdgeEnabled) {
                    val insets = ViewCompat.getRootWindowInsets(eventCalendarView)?.getInsets(
                        WindowInsetsCompat.Type.systemBars() + WindowInsetsCompat.Type.displayCutout()
                    )

                    val isPortrait = (binding.root.tag?.toString()?.toIntOrNull() ?: 0) == 0

                    val left = insets?.left ?: 0
                    val right = insets?.right ?: 0
                    val bottom = insets?.bottom ?: 0

                    if (isPortrait) {
                        listOf(
                            eventCalendarViewRow6.eventCalendarViewCalendarWeek.root,
                            eventCalendarViewRow6.eventCalendarViewDay1.root,
                            eventCalendarViewRow6.eventCalendarViewDay2.root,
                            eventCalendarViewRow6.eventCalendarViewDay3.root,
                            eventCalendarViewRow6.eventCalendarViewDay4.root,
                            eventCalendarViewRow6.eventCalendarViewDay5.root,
                            eventCalendarViewRow6.eventCalendarViewDay6.root,
                            eventCalendarViewRow6.eventCalendarViewDay7.root
                        ).forEach {
                            it.updatePadding(
                                bottom = bottom
                            )
                        }
                    } else {
                        eventCalendarViewLandscapeEdgeHelper?.updatePadding(
                            right = right
                        )

                        if (eventCalendarView.headerVisible) {
                            eventCalendarViewMonthYearHeader.updatePadding(
                                left = left
                            )
                        } else {
                            if (eventCalendarView.calendarWeekVisible) {
                                listOf(
                                    eventCalendarViewHeaderKw,
                                    eventCalendarViewRow1.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout,
                                    eventCalendarViewRow2.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout,
                                    eventCalendarViewRow3.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout,
                                    eventCalendarViewRow4.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout,
                                    eventCalendarViewRow5.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout,
                                    eventCalendarViewRow6.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewFrameLayout
                                ).forEach {
                                    it.updatePadding(
                                        left = left
                                    )
                                }
                            } else {
                                listOf(
                                    eventCalendarViewHeaderMonday,
                                    eventCalendarViewRow1.eventCalendarViewDay1.eventCalendarViewDayRecyclerView,
                                    eventCalendarViewRow2.eventCalendarViewDay1.eventCalendarViewDayRecyclerView,
                                    eventCalendarViewRow3.eventCalendarViewDay1.eventCalendarViewDayRecyclerView,
                                    eventCalendarViewRow4.eventCalendarViewDay1.eventCalendarViewDayRecyclerView,
                                    eventCalendarViewRow5.eventCalendarViewDay1.eventCalendarViewDayRecyclerView,
                                    eventCalendarViewRow6.eventCalendarViewDay1.eventCalendarViewDayRecyclerView
                                ).forEach {
                                    it.updatePadding(
                                        left = left
                                    )
                                }

                                listOf(
                                    eventCalendarViewRow1.eventCalendarViewDay1.eventCalendarViewDayTextView,
                                    eventCalendarViewRow2.eventCalendarViewDay1.eventCalendarViewDayTextView,
                                    eventCalendarViewRow3.eventCalendarViewDay1.eventCalendarViewDayTextView,
                                    eventCalendarViewRow4.eventCalendarViewDay1.eventCalendarViewDayTextView,
                                    eventCalendarViewRow5.eventCalendarViewDay1.eventCalendarViewDayTextView
                                ).forEach {
                                    it.updateLayoutParams<MarginLayoutParams> {
                                        marginStart = left
                                    }
                                }
                            }
                        }
                    }
                }

                eventCalendarViewMonthYearImageViewLeft.setOnClickListener {
                    eventCalendarView.binding.eventCalendarRecyclerView.smoothScrollTo(
                        currentItem - 1
                    )
                }

                eventCalendarViewMonthYearImageViewRight.setOnClickListener {
                    eventCalendarView.binding.eventCalendarRecyclerView.smoothScrollTo(
                        currentItem + 1
                    )
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(
            EcvEventCalendarViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
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

            eventCalendarViewMonthYearImageViewLeft.visibility =
                if (holder.bindingAdapterPosition == 0) View.INVISIBLE else View.VISIBLE

            eventCalendarViewMonthYearImageViewRight.visibility =
                if (holder.bindingAdapterPosition == itemCount.minus(1)) View.INVISIBLE else View.VISIBLE

            styleTextViews(
                days = month.getDaysOfMonthAndGivenYear(year),
                circleBindingList = holder.ecvTextviewCircleBindings(),
                cwBindingList = holder.ecvTextviewCwBinding()
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
        val totalMonths = this + eventCalendarView.sMonth
        val yearOffset = totalMonths / 12
        val month = totalMonths % 12
        val adjustedYear = eventCalendarView.sYear + yearOffset

        return SparseIntArray().apply {
            put(0, if (month < 0) month + 12 else month)
            put(1, adjustedYear)
        }
    }

    private fun styleTextViews(
        days: List<Day>,
        circleBindingList: List<EcvTextviewCircleBinding>,
        cwBindingList: List<EcvTextviewCwBinding>
    ) {
        for ((index, day) in days.withIndex()) {
            val dayItemLayout = circleBindingList[index]

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
                        eventItemTextColor = eventCalendarView.eventItemTextColor,
                        eventItemDarkTextColor = eventCalendarView.eventItemDarkTextColor
                    )
                }
            }

            with(textView) {
                text = day.value

                if (day.isCurrentDay) {
                    setTextColor(eventCalendarView.currentDayTextColor)

                    context.getRealContext()?.let {
                        background = ContextCompat.getDrawable(it, R.drawable.ecv_circle)
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

            if (eventCalendarView._calendarWeekVisible) {
                initCalendarWeek(day, index, cwBindingList)
            }
        }
    }

    private fun initCalendarWeek(
        day: Day, index: Int, ecvTextviewCwBinding: List<EcvTextviewCwBinding>
    ) {
        when (index) {
            0 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(0, day)
            }

            7 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(1, day)
            }

            14 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(2, day)
            }

            21 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(3, day)
            }

            28 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(4, day)
            }

            35 -> {
                ecvTextviewCwBinding.setCalendarWeekUi(5, day)
            }
        }
    }

    private fun List<EcvTextviewCwBinding>.setCalendarWeekUi(index: Int, day: Day) {
        val textView = getOrNull(index)?.eventCalendarViewDayTextView ?: return
        with(textView) {
            if (day.isCurrentMonth || day.isCurrentDay) {
                setTypeface(typeface, Typeface.BOLD)
            } else {
                setTypeface(typeface, Typeface.ITALIC)
            }
            text = day.convertStringToCalendarWeek()
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
                        textView, ColorStateList.valueOf(eventCalendarView.countBackgroundTintColor)
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