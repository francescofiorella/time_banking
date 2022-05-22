package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.R
import com.polito.timebanking.models.TimeSlot
import java.util.*

class TimeSlotAdapter(
    private var data: List<TimeSlot>,
    private val listener: TimeSlotListener
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>(), Filterable {

    private val dataFiltered: List<TimeSlot> = data

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

            edit.isVisible = timeSlot.email == Firebase.auth.currentUser?.email
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

    fun updateData(newList: List<TimeSlot>) {
        val diffUtil = TimeSlotDiffUtil(data, newList)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        data = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val filterResults = FilterResults()

                if (charSequence.isNullOrEmpty()) {
                    filterResults.count = dataFiltered.size
                    filterResults.values = dataFiltered
                } else {
                    val list = dataFiltered.filter {
                        it.title.lowercase(Locale.getDefault()).contains(
                            charSequence.toString()
                                .lowercase(Locale.getDefault())
                        )
                    }

                    filterResults.values = list
                    filterResults.count = list.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                updateData(results?.values as List<TimeSlot>)
            }
        }
    }

    fun sortByTitle() {
        val list = dataFiltered.sortedBy {
            it.title
        }
        updateData(list)
    }

    fun sortByDate() {
        val list = dataFiltered.sortedBy {
            it.year
            it.month
            it.day
            it.hour
            it.minute
        }
        updateData(list)
    }

    fun filterDuration(duration: String) {
        val list = dataFiltered.filter {
            it.duration == duration
        }
        updateData(list)
    }

    fun clearFilter() {
        updateData(dataFiltered)
    }
}