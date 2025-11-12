package com.nmd.eventCalendar.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Typeface
import android.graphics.drawable.RippleDrawable
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.RestrictTo
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
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
import com.nmd.eventCalendar.utils.Utils.Companion.expressiveCwHelper
import com.nmd.eventCalendar.utils.Utils.Companion.getDaysOfMonthAndGivenYear
import com.nmd.eventCalendar.utils.Utils.Companion.getDimensInt
import com.nmd.eventCalendar.utils.Utils.Companion.getMonthName
import com.nmd.eventCalendar.utils.Utils.Companion.getTextTypeface
import com.nmd.eventCalendar.utils.Utils.Companion.orEmptyArrayList
import com.nmd.eventCalendar.utils.Utils.Companion.orTrue
import com.nmd.eventCalendar.utils.Utils.Companion.setItemTint
import com.nmd.eventCalendar.utils.Utils.Companion.smoothScrollTo
import java.util.Calendar

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class InfiniteAdapter(
    private val eventCalendarView: EventCalendarView
) : RecyclerView.Adapter<InfiniteAdapter.AdapterViewHolder>() {

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

                eventCalendarViewLinearLayoutCompat.showDividers =
                    if (eventCalendarView.expressiveUi) {
                        LinearLayoutCompat.SHOW_DIVIDER_NONE
                    } else {
                        LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
                    }

                listOf(
                    eventCalendarViewRow1.eventCalendarViewRowsLinearLayoutCompat,
                    eventCalendarViewRow2.eventCalendarViewRowsLinearLayoutCompat,
                    eventCalendarViewRow3.eventCalendarViewRowsLinearLayoutCompat,
                    eventCalendarViewRow4.eventCalendarViewRowsLinearLayoutCompat,
                    eventCalendarViewRow5.eventCalendarViewRowsLinearLayoutCompat,
                    eventCalendarViewRow6.eventCalendarViewRowsLinearLayoutCompat
                ).forEach {
                    if (eventCalendarView.expressiveUi) {
                        it.showDividers = LinearLayoutCompat.SHOW_DIVIDER_NONE
                    } else {
                        it.showDividers = LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
                    }
                }

                listOf(
                    eventCalendarViewHeaderCw,
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
                    ViewCompat.setOnApplyWindowInsetsListener(eventCalendarView) { view, windowInsets ->
                        val insets =
                            windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())

                        applyWindowInsets(insets = insets, binding = binding)

                        ViewCompat.onApplyWindowInsets(view, windowInsets)
                    }

                    val insets = ViewCompat.getRootWindowInsets(eventCalendarView)?.getInsets(
                        WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
                    )
                    if (insets != null) {
                        eventCalendarView.requestApplyInsets()
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

    private fun applyWindowInsets(insets: Insets, binding: EcvEventCalendarViewBinding) =
        with(binding) {
            val isPortrait = (root.tag?.toString()?.toIntOrNull() ?: 0) == 0

            if (isPortrait) {
                portaitHelper(binding = binding, bottom = insets.bottom)
            } else {
                landscapeHelper(
                    binding = binding,
                    left = insets.left,
                    right = insets.right,
                    bottom = insets.bottom
                )
            }
        }

    private fun portaitHelper(binding: EcvEventCalendarViewBinding, bottom: Int): Unit =
        with(binding) {
            eventCalendarViewRow6.eventCalendarViewCalendarWeek.root.updatePadding(
                bottom = bottom
            )

            listOf(
                eventCalendarViewRow6.eventCalendarViewDay1.root,
                eventCalendarViewRow6.eventCalendarViewDay2.root,
                eventCalendarViewRow6.eventCalendarViewDay3.root,
                eventCalendarViewRow6.eventCalendarViewDay4.root,
                eventCalendarViewRow6.eventCalendarViewDay5.root,
                eventCalendarViewRow6.eventCalendarViewDay6.root,
                eventCalendarViewRow6.eventCalendarViewDay7.root
            ).forEach {
                if (eventCalendarView.expressiveUi) {
                    it.updateLayoutParams<MarginLayoutParams> {
                        bottomMargin = bottom
                    }
                } else {
                    it.updatePadding(
                        bottom = bottom
                    )
                }
            }
        }

    private fun landscapeHelper(
        binding: EcvEventCalendarViewBinding,
        left: Int,
        right: Int,
        bottom: Int
    ): Unit =
        with(binding) {
            eventCalendarViewLandscapeEdgeHelper?.updatePadding(
                right = right
            )

            if (eventCalendarView.expressiveUi) {
                eventCalendarViewNestedScrollView?.updatePadding(
                    bottom = bottom
                )
            }

            // The header is visible
            if (eventCalendarView.headerVisible) {
                eventCalendarViewMonthYearHeader.updatePadding(
                    left = left
                )
                eventCalendarViewMonthYearImageViewRight.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = bottom
                }

                return
            }

            // The header is not visible but the calendar week is visible
            if (eventCalendarView.calendarWeekVisible) {
                listOf(
                    eventCalendarViewHeaderCw,
                    eventCalendarViewRow1.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout,
                    eventCalendarViewRow2.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout,
                    eventCalendarViewRow3.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout,
                    eventCalendarViewRow4.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout,
                    eventCalendarViewRow5.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout,
                    eventCalendarViewRow6.eventCalendarViewCalendarWeek.eventCalendarViewDayTextViewInsetsFrameLayout
                ).forEach {
                    it.updatePadding(
                        left = left
                    )
                }
                return
            }

            // The header is not visible and the calendar week is not visible
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
                eventCalendarViewRow5.eventCalendarViewDay1.eventCalendarViewDayTextView,
                eventCalendarViewRow6.eventCalendarViewDay1.eventCalendarViewDayTextView
            ).forEach {
                it.updateLayoutParams<MarginLayoutParams> {
                    marginStart = left.div(2)
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

            val calendar = Calendar.getInstance()
            // Only update the current day if it's the current year and month
            if (holder.yearAdapterViewHolder == year && calendar.get(Calendar.MONTH) == month) {
                val currentDayView = when (calendar.get(Calendar.DAY_OF_WEEK)) {
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
                currentDayView.setTextColor(eventCalendarView.currentWeekdayTextColor)
            }
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

            with(dayItemLayout.eventCalendarViewDayLinearLayoutCompat) {
                setOnClickListener {
                    eventCalendarView.clickListener?.onClick(day)
                }

                background = if (eventCalendarView.expressiveUi) {
                    val ripple = ContextCompat.getDrawable(
                        context, when (index) {
                            0 -> {
                                // Top left rounded
                                R.drawable.ecv_ripple_expressive_top_left
                            }

                            6 -> {
                                // Top right rounded
                                R.drawable.ecv_ripple_expressive_top_right
                            }

                            35 -> {
                                // Bottom left rounded
                                R.drawable.ecv_ripple_expressive_bottom_left
                            }

                            41 -> {
                                // Bottom right rounded
                                R.drawable.ecv_ripple_expressive_bottom_right
                            }

                            else -> {
                                // Default rounded
                                R.drawable.ecv_ripple_expressive_default
                            }
                        }
                    ) as RippleDrawable
                    ripple.setItemTint(
                        eventCalendarView.expressiveDayBackgroundTintColor
                    )
                    ripple
                } else {
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ecv_ripple_default
                    )
                }
            }

            dayItemLayout.root.showDividers = if (eventCalendarView.expressiveUi) {
                LinearLayoutCompat.SHOW_DIVIDER_NONE
            } else {
                LinearLayoutCompat.SHOW_DIVIDER_BEGINNING
            }

            with(dayItemLayout.eventCalendarViewDayRecyclerView) {
                val eventList = day.dayEvents(eventCalendarView.eventArrayList.orEmptyArrayList())

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

                if (eventList.isNotEmpty() && eventCalendarView.countVisible) {
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

            with(dayItemLayout.eventCalendarViewDayTextView) {
                text = day.value

                if (day.isCurrentDay) {
                    setTextColor(eventCalendarView.currentDayTextColor)

                    background = ContextCompat.getDrawable(
                        context,
                        if (eventCalendarView.expressiveUi) R.drawable.ecv_expressive_circle else R.drawable.ecv_circle
                    )

                    ViewCompat.setBackgroundTintList(
                        this,
                        ColorStateList.valueOf(eventCalendarView.currentDayBackgroundTintColor)
                    )
                }

                updateLayoutParams {
                    height = if (eventCalendarView.expressiveUi) {
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    } else {
                        R.dimen.dp_current_day_height.getDimensInt(context.resources)
                    }
                }

                setTypeface(typeface, day.getTextTypeface())
            }

            if (eventCalendarView.isCalendarWeekVisible) {
                initCalendarWeek(day = day, index = index, ecvTextviewCwBinding = cwBindingList)
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
        val frameLayout =
            getOrNull(index)?.eventCalendarViewDayTextViewExpressiveFrameLayout ?: return
        val textView = getOrNull(index)?.eventCalendarViewDayTextView ?: return
        with(textView) {
            text = day.convertStringToCalendarWeek()
        }

        val root = getOrNull(index)?.root ?: return
        root.showDividers = if (eventCalendarView.isExpressiveUi) {
            LinearLayoutCompat.SHOW_DIVIDER_NONE
        } else {
            LinearLayoutCompat.SHOW_DIVIDER_BEGINNING
        }

        eventCalendarView.expressiveUi.expressiveCwHelper(
            frameLayout = frameLayout,
            index = index,
            cwBackgroundTintColor = eventCalendarView.expressiveCwBackgroundTintColor
        )
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