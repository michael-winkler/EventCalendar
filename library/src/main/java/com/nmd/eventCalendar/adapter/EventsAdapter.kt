package com.nmd.eventCalendar.adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.nmd.eventCalendar.R
import com.nmd.eventCalendar.databinding.EcvEventViewBinding
import com.nmd.eventCalendar.model.Event
import com.nmd.eventCalendar.utils.Utils.Companion.isDarkColor

class EventsAdapter(
    private var list: ArrayList<Event>,
    private val eventItemAutomaticTextColor: Boolean,
    private val eventItemTextColor: Int,
) :
    RecyclerView.Adapter<EventsAdapter.AdapterViewHolder>() {

    class AdapterViewHolder(val binding: EcvEventViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(
            EcvEventViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        with(holder.binding) {
            val item = list.getOrNull(position) ?: return
            itemEventMaterialTextView.text = item.name

            val color = Color.parseColor(item.backgroundHexColor)
            if (eventItemAutomaticTextColor) {
                itemEventMaterialTextView.setTextColor(
                    ContextCompat.getColor(
                        itemEventMaterialTextView.context,
                        if (color.isDarkColor()) R.color.ecv_white else R.color.ecv_charcoal_color

                    )
                )
            } else {
                itemEventMaterialTextView.setTextColor(eventItemTextColor)
            }

            ViewCompat.setBackgroundTintList(
                itemEventMaterialTextView,
                ColorStateList.valueOf(color)
            )
        }
    }

    override fun getItemCount(): Int = list.size

}