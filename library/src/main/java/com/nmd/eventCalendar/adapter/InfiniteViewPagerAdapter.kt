package com.nmd.eventCalendar.adapter

import android.util.SparseIntArray
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.nmd.eventCalendar.EventCalendarView
import com.nmd.eventCalendar.builder.EventCalendarBuilder

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
        return EventCalendarBuilder().date(month = item.get(0), year = item.get(1))
            .eventCalendarView(eventCalendarView).build()
    }

    override fun getItemId(position: Int): Long {
        val item = position.calculate()
        return item.get(0).toLong() * 100L + item.get(1).toLong()
    }

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

}