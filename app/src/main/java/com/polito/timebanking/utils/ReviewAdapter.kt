package com.polito.timebanking.utils

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
import com.google.firebase.firestore.FirebaseFirestore
import com.polito.timebanking.R
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.models.User
import java.util.*

class ReviewAdapter(
    private var data: List<Feedback>
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>(), Filterable {

    private val dataFiltered: List<Feedback> = data

    class ReviewViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        private val reviewRB: RatingBar = v.findViewById(R.id.review_rb)
        private val reviewTV: TextView = v.findViewById(R.id.review_tv)
        private val writerTV: TextView = v.findViewById(R.id.full_name_tv)
        private val writerIV: ImageView = v.findViewById(R.id.photo_iv)

        fun bind(feedback: Feedback) {
            reviewRB.rating = feedback.rate
            reviewTV.text = feedback.comment
            getWriterInfo(feedback.writeruid,writerTV,writerIV)

        }


        private fun getWriterInfo(uid: String, writerTV: TextView, writerIV: ImageView){
            db.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener {
                    val writer = it.toObject(User::class.java)
                    if(writer!=null){
                        writerTV.setText(writer.fullName)
                        Glide.with(writerIV)
                            .load(writer?.photoUrl)
                            .apply(RequestOptions.circleCropTransform())
                            .into(writerIV)
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_review_item, parent, false)
        return ReviewViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = data[position]
        holder.bind(
            review
        )
    }

    override fun getItemCount(): Int = data.size

    fun updateData(newList: List<Feedback>) {
        val diffUtil = ReviewDiffUtil(data, newList)
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
                        it.comment?.lowercase(Locale.getDefault())?.contains(
                            charSequence.toString()
                                .lowercase(Locale.getDefault())
                        ) ?: false
                    }

                    filterResults.values = list
                    filterResults.count = list.size
                }
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                @Suppress("UNCHECKED_CAST")
                updateData(results?.values as List<Feedback>)
            }
        }
    }

    fun sortByRate() {
        val list = dataFiltered.sortedBy {
            it.rate
        }
        updateData(list)
    }

    fun filterRate(rate: Int) {
        val list = dataFiltered.filter {
            it.rate.toInt() == rate
        }
        updateData(list)
    }

    fun clearFilter() {
        updateData(dataFiltered)
    }
}