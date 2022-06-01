package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.polito.timebanking.R
import com.polito.timebanking.models.ChatMessage
import java.text.SimpleDateFormat
import java.util.*

class ChatMessageAdapter(
    private val data: List<ChatMessage>,
    private val loggedUserId: String
) : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    class ChatMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val otherUserLayout: ConstraintLayout = v.findViewById(R.id.other_user_layout)
        private val otherUserDateTV: MaterialTextView = v.findViewById(R.id.other_user_date_tv)
        private val otherUserMessageTV: MaterialTextView = v.findViewById(R.id.other_user_message_tv)
        private val otherUserTimeTV: MaterialTextView = v.findViewById(R.id.other_user_time_tv)

        private val userLayout: ConstraintLayout = v.findViewById(R.id.user_layout)
        private val userDateTV: MaterialTextView = v.findViewById(R.id.user_date_tv)
        private val userMessageTV: MaterialTextView = v.findViewById(R.id.user_message_tv)
        private val userTimeTV: MaterialTextView = v.findViewById(R.id.user_time_tv)

        fun bind(chatMessage: ChatMessage, loggedUserId: String, hasPreviousWithSameDate: Boolean) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(chatMessage.timestamp!!)

            otherUserDateTV.isVisible = !hasPreviousWithSameDate
            userDateTV.isVisible = !hasPreviousWithSameDate

            if (chatMessage.from == loggedUserId) {
                otherUserLayout.isVisible = false
                userLayout.isVisible = true
                userMessageTV.text = chatMessage.body
                userDateTV.text = dateFormat.format(date)
                userTimeTV.text = timeFormat.format(date)
            } else {
                otherUserLayout.isVisible = true
                userLayout.isVisible = false
                otherUserMessageTV.text = chatMessage.body
                otherUserDateTV.text = dateFormat.format(date)
                otherUserTimeTV.text = timeFormat.format(date)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessageViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_chat_message_item, parent, false)
        return ChatMessageViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ChatMessageViewHolder, position: Int) {
        val chatMessage = data[position]

        val hasPreviousWithSameDate = if (position > 0) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(chatMessage.timestamp!!)
            val previousDate = Date(data[position - 1].timestamp!!)

            dateFormat.format(date) == dateFormat.format(previousDate)
        } else {
            false
        }
        holder.bind(chatMessage, loggedUserId, hasPreviousWithSameDate)
    }

    override fun getItemCount(): Int = data.size
}