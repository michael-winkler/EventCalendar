package com.nmd.eventCalendar.custom

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

/**
 * Credit to: Nick Rout - https://medium.com/@ricknout
 * https://medium.com/over-engineering/detecting-snap-changes-with-androids-recyclerview-snaphelper-9e9f5e95c424
 */
class SnapOnScrollListener(
    private val snapHelper: SnapHelper,
    private var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
    private var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null,
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
            && newState == RecyclerView.SCROLL_STATE_IDLE
        ) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }

    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }

    interface OnSnapPositionChangeListener {
        fun onSnapPositionChange(position: Int)
    }

    companion object {
        fun RecyclerView.attachSnapHelperWithListener(
            snapHelper: SnapHelper,
            behavior: Behavior = Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
            onSnapPositionChangeListener: OnSnapPositionChangeListener,
        ) {
            if (onFlingListener == null) {
                // Only add it if there was no one before
                snapHelper.attachToRecyclerView(this)
            }
            val snapOnScrollListener =
                SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
            addOnScrollListener(snapOnScrollListener)
        }
    }

}