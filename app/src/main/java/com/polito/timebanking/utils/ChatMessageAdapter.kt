package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.polito.timebanking.R
import com.polito.timebanking.models.ChatMessage

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
            otherUserDateTV.isVisible = !hasPreviousWithSameDate
            userDateTV.isVisible = !hasPreviousWithSameDate

            if (chatMessage.from == loggedUserId) {
                otherUserLayout.isVisible = false
                userLayout.isVisible = true
                userMessageTV.text = chatMessage.body
                chatMessage.timestamp?.let {
                    userDateTV.text = timestampToDateString(it)
                    userTimeTV.text = timestampToTimeString(it)
                }

            } else {
                otherUserLayout.isVisible = true
                userLayout.isVisible = false
                otherUserMessageTV.text = chatMessage.body
                chatMessage.timestamp?.let {
                    otherUserDateTV.text = timestampToDateString(it)
                    otherUserTimeTV.text = timestampToTimeString(it)
                }
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
            if (chatMessage.timestamp != null && data[position - 1].timestamp != null) {
                timestampToDateString(chatMessage.timestamp) == timestampToDateString(data[position - 1].timestamp!!)
            } else {
                false
            }
        } else {
            false
        }
        holder.bind(chatMessage, loggedUserId, hasPreviousWithSameDate)
    }

    override fun getItemCount(): Int = data.size
}