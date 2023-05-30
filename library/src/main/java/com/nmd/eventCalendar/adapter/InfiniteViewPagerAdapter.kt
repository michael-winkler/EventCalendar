package com.nmd.eventCalendar.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.builder.EventCalendarBuilder
import java.util.Calendar

class InfiniteViewPagerAdapter(
    val fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
    val eventCalendarView: EventCalendarView,
) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int {
        return getMonthCount()
    }

    override fun createFragment(position: Int): Fragment {
        val item = position.calculate()
        return EventCalendarBuilder().date(month = item.second, year = item.first)
            .eventCalendarView(eventCalendarView).build()
    }

    override fun getItemId(position: Int): Long {
        val item = position.calculate()
        return item.first.toLong() * 100L + item.second.toLong()
    }

    private fun getMonthCount(): Int {
        val startCalendar = Calendar.getInstance()
        startCalendar.set(eventCalendarView.sYear, eventCalendarView.sMonth, 1)
        val endCalendar = Calendar.getInstance()
        endCalendar.set(eventCalendarView.eYear, eventCalendarView.eMonth, 1)
        val diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR)
        val diffMonth = endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH)
        val diffTotal = diffYear * 12 + diffMonth + 1
        return if (diffTotal > 0) diffTotal else 0
    }

    private fun Int.calculate(): Pair<Int, Int> {
        val year = eventCalendarView.sYear + this / 12
        val month = (this % 12 + eventCalendarView.sMonth) % 12
        return Pair(first = year, second = month)
    }

}
