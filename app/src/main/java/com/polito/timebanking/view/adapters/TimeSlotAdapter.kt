package com.polito.timebanking.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.R
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.models.TimeSlot
import java.util.*

class TimeSlotAdapter(
    private var data: List<TimeSlot>,
    private val listener: TimeSlotListener
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>(), Filterable {

    private val dataFiltered: List<TimeSlot> = data

    class TimeSlotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        private val cardLayout: MaterialCardView = v.findViewById(R.id.layout)
        private val titleTV: TextView = v.findViewById(R.id.title_tv)
        private val fullNameTV: TextView = v.findViewById(R.id.person_tv)
        private val locationTV: TextView = v.findViewById(R.id.location_tv)
        private val dateTV: TextView = v.findViewById(R.id.date_tv)
        private val timeCreditTV: TextView = v.findViewById(R.id.time_credit_tv)
        private val editFAB: FloatingActionButton = v.findViewById(R.id.edit_btn)
        private val feedbackBTN: Button = v.findViewById(R.id.feedback_btn)

        fun bind(
            timeSlot: TimeSlot,
            cardAction: (v: View) -> Unit,
            editAction: (v: View) -> Unit,
            FeedbackAction: (v: View) -> Unit
        ) {
            titleTV.text = timeSlot.title
            loadUserInfo(timeSlot.uid, fullNameTV)
            locationTV.text = timeSlot.location
            dateTV.text = timeSlot.getDate()
            val timeCredit =
                "${timeSlot.timeCredit} ${if (timeSlot.timeCredit == 1) "hour" else "hours"}"
            timeCreditTV.text = timeCredit
            cardLayout.setOnClickListener(cardAction)
            editFAB.setOnClickListener(editAction)
            feedbackBTN.setOnClickListener(FeedbackAction)

            editFAB.isVisible = timeSlot.uid == Firebase.auth.currentUser?.uid

            if (timeSlot.state == "completed") {
                isReviewed(Firebase.auth.currentUser?.uid, timeSlot.id, feedbackBTN)
            } else {
                feedbackBTN.isVisible = false
            }

        }

        private fun loadUserInfo(uid: String, textView: TextView) {
            db.collection("users")
                .document(uid)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        textView.text = v?.getString("fullName")
                    }
                }
        }

        private fun isReviewed(uid: String?, tsid: String, feedbackbtn: Button) {
            db.collection("feedback")
                .whereEqualTo("tsid", tsid)
                .whereEqualTo("writeruid", uid)
                .get()
                .addOnSuccessListener {
                    val feedback = it.toObjects(Feedback::class.java)
                    if (feedback.size != 0) {
                        println("Ho letto: $feedback")
                        feedbackbtn.alpha = .5f
                        feedbackbtn.isClickable = false
                        feedbackbtn.text = "Feedback sent"
                    } else {
                        feedbackbtn.isVisible
                    }
                }
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
            { listener.onEditClickListener(timeSlot, position) },
            { listener.onFeedbackClickListener(timeSlot, position) }

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

    fun sortByTimeCredit() {
        val list = dataFiltered.sortedBy {
            it.timeCredit
        }
        updateData(list)
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

    fun filterTimeCredit(filter: (Int) -> Boolean) {
        val list = dataFiltered.filter {
            filter(it.timeCredit)
        }
        updateData(list)
    }

    fun clearFilter() {
        updateData(dataFiltered)
    }
}