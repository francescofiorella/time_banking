package com.polito.timebanking.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.R
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.models.User
import java.util.*

class TimeSlotAdapter(
    private var data: List<TimeSlot>,
    private val listener: TimeSlotListener
) : RecyclerView.Adapter<TimeSlotAdapter.TimeSlotViewHolder>(), Filterable {

    private val dataFiltered: List<TimeSlot> = data

    class TimeSlotViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
        private val auth = Firebase.auth

        private val cardLayout: MaterialCardView = v.findViewById(R.id.layout)
        private val photoIv : ImageView = v.findViewById(R.id.photo_iv)
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
            FeedbackAction: (v: View) -> Unit,
            completeAction: (v: View) -> Unit
        ) {
            titleTV.text = timeSlot.title
            loadUserInfo(timeSlot.uid, fullNameTV, photoIv)
            locationTV.text = timeSlot.location
            dateTV.text = timeSlot.getDate()
            val timeCredit =
                "${timeSlot.timeCredit} ${if (timeSlot.timeCredit == 1) "hour" else "hours"}"
            timeCreditTV.text = timeCredit
            cardLayout.setOnClickListener(cardAction)
            editFAB.setOnClickListener(editAction)
            feedbackBTN.setOnClickListener(FeedbackAction)

            editFAB.isVisible = timeSlot.uid == auth.currentUser?.uid

            if (timeSlot.state == "completed" && (auth.currentUser?.uid==timeSlot.uid ||auth.currentUser?.uid==timeSlot.cid)) {
                isReviewed(auth.currentUser?.uid, timeSlot.id, feedbackBTN)
            } else {
                feedbackBTN.isVisible = false
            }

            if (timeSlot.state == "accepted" && auth.currentUser?.uid == timeSlot.uid && isTimeslotInThePast(timeSlot)) {
                feedbackBTN.text = "Mark as completed"
                feedbackBTN.isVisible = true
                feedbackBTN.setOnClickListener(completeAction)
            }
        }

        private fun isTimeslotInThePast(timeSlot: TimeSlot) : Boolean {
            val calendar = Calendar.getInstance(Locale.getDefault())
            return if (timeSlot.year < calendar.get(Calendar.YEAR)) {
                true
            } else if (timeSlot.year > calendar.get(Calendar.YEAR)) {
                false
            } else if (timeSlot.month < (calendar.get(Calendar.MONTH) + 1)) {
                true
            } else if (timeSlot.month > (calendar.get(Calendar.MONTH) + 1)) {
                false
            } else if (timeSlot.day < calendar.get(Calendar.DAY_OF_MONTH)) {
                true
            } else if (timeSlot.day > calendar.get(Calendar.DAY_OF_MONTH)) {
                false
            } else if (timeSlot.hour < calendar.get(Calendar.HOUR_OF_DAY)) {
                true
            } else if (timeSlot.hour > calendar.get(Calendar.HOUR_OF_DAY)) {
                false
            } else {
                timeSlot.minute < calendar.get(Calendar.MINUTE)
            }
        }

        private fun loadUserInfo(uid: String, textView: TextView, photo: ImageView) {
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener{
                    val writer = it.toObject(User::class.java)
                    if(writer != null){
                        if (writer != null)
                        textView.text = writer.fullName
                        Glide.with(photoIv)
                            .load(writer.photoUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(photoIv)
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
            { listener.onFeedbackClickListener(timeSlot, position) },
            { listener.onCompleteClickListener(timeSlot, position) }
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