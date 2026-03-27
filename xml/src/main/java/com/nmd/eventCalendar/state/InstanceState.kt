package com.nmd.eventCalendar.state

import androidx.annotation.Keep
import androidx.annotation.RestrictTo
import com.nmd.eventCalendar.model.Event
import java.util.Calendar

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
internal class InstanceState {

    companion object {
        private var instanceStateModels = ArrayList<InstanceStateModel>()
    }

    fun saveInstanceState(id: Int, stateModel: StateModel) {
        val found = instanceStateModels.any { it.id == id }
        if (found) return

        instanceStateModels.add(InstanceStateModel(id, stateModel))
    }

    fun removeArrayListEventById(id: Int) {
        instanceStateModels = ArrayList(instanceStateModels.filter { it.id != id })
    }

    fun restoreInstanceState(id: Int): StateModel? {
        return instanceStateModels.find { it.id == id }?.stateModel
    }

    @Keep
    class InstanceStateModel(
        val id: Int,
        val stateModel: StateModel
    )

    @Keep
    class StateModel(
        var disallowIntercept: Boolean = true,
        var calendarWeekVisible: Boolean = false,
        var isExpressiveUi: Boolean = false,
        var currentRecyclerViewPosition: Int = 0,
        var startMonth: Int = Calendar.JANUARY,
        var startYear: Int = 2020,
        var endMonth: Int = Calendar.DECEMBER,
        var endYear: Int = Calendar.getInstance().get(Calendar.YEAR).plus(1),
        var currentYearAndMonthPair: Pair<Int, Int> = Pair(0, 0),
        var events: ArrayList<Event> = arrayListOf()
    )

}