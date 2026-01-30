package com.nmd.eventCalendar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.RecyclerView
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendarSample.databinding.RecyclerViewSheetEventBinding

class SheetEventsAdapter(
    private var list: ArrayList<Event>
) : RecyclerView.Adapter<SheetEventsAdapter.AdapterViewHolder>() {

    class AdapterViewHolder(val binding: RecyclerViewSheetEventBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(
            RecyclerViewSheetEventBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) = with(holder.binding) {
        val item = list[holder.bindingAdapterPosition]
        itemEventMaterialTextView.text = item.name

        val color = item.backgroundHexColor.toColorInt()
        itemEventMaterialTextView.setTextColor(
            ContextCompat.getColor(
                itemEventMaterialTextView.context,
                if (color.isDarkColor()) R.color.ecv_white else R.color.ecv_black
            )
        )
        root.setCardBackgroundColor(color)
    }

    private fun Int.isDarkColor(): Boolean {
        return ColorUtils.calculateLuminance(this) < 0.5
    }

    override fun getItemCount(): Int = list.size

}