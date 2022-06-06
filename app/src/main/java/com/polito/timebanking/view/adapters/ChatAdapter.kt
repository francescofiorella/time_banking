package com.polito.timebanking.view.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.polito.timebanking.R
import com.polito.timebanking.models.Chat
import com.polito.timebanking.utils.timestampToDateString

class ChatAdapter(
    private val data: List<Chat>,
    private val loggedUserId: String,
    private val listener: ChatListener
) : RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    class ChatViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

        private val layout = v.findViewById<ConstraintLayout>(R.id.chat_layout)
        private val userPicIV = v.findViewById<ImageView>(R.id.user_pic_iv)
        private val userNameTV = v.findViewById<TextView>(R.id.user_name_tv)
        private val timeslotTV = v.findViewById<TextView>(R.id.timeslot_name_tv)
        private val dateTV = v.findViewById<TextView>(R.id.date_tv)

        fun bind(chat: Chat, loggedUserId: String, onClickAction: (v: View) -> Unit) {
            layout.setOnClickListener(onClickAction)
            chat.timestamp?.let {
                dateTV.text = timestampToDateString(it)
            }
            chat.tsid?.let {
                loadTimeslotInfo(it, timeslotTV)
            }
            chat.uids?.let {
                val index = if (it[0] == loggedUserId) {
                    1
                } else {
                    0
                }
                loadUserInfo(it[index], userNameTV, userPicIV)
            }
        }

        private fun loadTimeslotInfo(tsid: String, textView: TextView) {
            db.collection("timeslot")
                .document(tsid)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        textView.text = v?.getString("title")
                    }
                }
        }

        private fun loadUserInfo(uid: String, textView: TextView, imageView: ImageView) {
            db.collection("users")
                .document(uid)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        textView.text = v?.getString("fullName")
                        val photoUrl = v?.getString("photoUrl")
                        photoUrl?.let {
                            Glide.with(imageView)
                                .load(it)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imageView)
                        }
                    }
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val vg = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.layout_chat_list_item, parent, false)
        return ChatViewHolder(vg)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = data[position]
        holder.bind(chat, loggedUserId) { listener.onChatClickListener(chat, position) }
    }

    override fun getItemCount(): Int = data.size
}