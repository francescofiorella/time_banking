package com.polito.timebanking.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.polito.timebanking.R
import com.polito.timebanking.models.ChatMessage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatMessageAdapter(
    private val data: List<ChatMessage>,
    private val loggedUserId: String
) : RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>() {

    class ChatMessageViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val otherUserLayout: ConstraintLayout = v.findViewById(R.id.other_user_layout)
        private val otherUserDateTV: TextView = v.findViewById(R.id.other_user_date_tv)
        private val otherUserMessageTV: TextView = v.findViewById(R.id.other_user_message_tv)
        private val otherUserTimeTV: TextView = v.findViewById(R.id.other_user_time_tv)

        private val userLayout: ConstraintLayout = v.findViewById(R.id.user_layout)
        private val userDateTV: TextView = v.findViewById(R.id.user_date_tv)
        private val userMessageTV: TextView = v.findViewById(R.id.user_message_tv)
        private val userTimeTV: TextView = v.findViewById(R.id.user_time_tv)

        fun bind(chatMessage: ChatMessage, loggedUserId: String) {
            val date = Instant.ofEpochSecond(chatMessage.timestamp!!)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            if (chatMessage.from == loggedUserId) {
                otherUserLayout.isVisible = false
                userLayout.isVisible = true
                userMessageTV.text = chatMessage.body
                userDateTV.text = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                userTimeTV.text = date.format(DateTimeFormatter.ofPattern("HH:mm"))
            } else {
                otherUserLayout.isVisible = true
                userLayout.isVisible = false
                otherUserMessageTV.text = chatMessage.body
                otherUserDateTV.text = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                otherUserTimeTV.text = date.format(DateTimeFormatter.ofPattern("HH:mm"))
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
        holder.bind(chatMessage, loggedUserId)
    }

    override fun getItemCount(): Int = data.size
}