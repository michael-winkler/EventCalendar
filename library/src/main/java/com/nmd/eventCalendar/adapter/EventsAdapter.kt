package com.nmd.eventCalendar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        val binding = EcvEventViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AdapterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        with(holder) {
            val item = list.getOrNull(position) ?: return
            binding.itemEventMaterialTextView.text = item.name

            val color = Color.parseColor(item.backgroundHexColor)
            if (eventItemAutomaticTextColor) {
                binding.itemEventMaterialTextView.setTextColor(
                    ContextCompat.getColor(
                        binding.itemEventMaterialTextView.context,
                        if (color.isDarkColor()) R.color.ecv_white else R.color.ecv_charcoal_color

                    )
                )
            } else {
                binding.itemEventMaterialTextView.setTextColor(eventItemTextColor)
            }

            binding.root.setCardBackgroundColor(color)
        }
    }

    override fun getItemCount(): Int = list.size

}