package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.polito.timebanking.R
import com.polito.timebanking.models.TimeSlot

class TimeSlotAdapter(
    private val data: MutableList<TimeSlot>,
    private val listener: TimeSlotListener
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>() {

    class TimeSlotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val card: MaterialCardView = v.findViewById(R.id.card_layout)
        private val title: TextView = v.findViewById(R.id.title_tv)
        private val location: TextView = v.findViewById(R.id.location_tv)
        private val date: TextView = v.findViewById(R.id.date_tv)
        private val edit: FloatingActionButton = v.findViewById(R.id.edit_fab)

        fun bind(timeSlot: TimeSlot, cardAction: (v: View) -> Unit, fabAction: (v: View) -> Unit) {
            title.text = timeSlot.title
            location.text = timeSlot.location
            date.text = timeSlot.getDate()
            card.setOnClickListener(cardAction)
            edit.setOnClickListener(fabAction)
        }

        fun unbind() {
            card.setOnClickListener(null)
            edit.setOnClickListener(null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeSlotViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_time_slot_item, parent, false)
        return TimeSlotViewHolder(vg)
    }

    override fun onBindViewHolder(holder: TimeSlotViewHolder, position: Int) {
        val timeSlot = data[position]
        holder.bind(
            timeSlot,
            { listener.onCardClickListener(timeSlot, position) },
            { listener.onEditClickListener(timeSlot, position) }
        )
    }

    override fun getItemCount(): Int = data.size
}