package com.polito.timebanking.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.Chat
import com.polito.timebanking.models.ChatMessage
import com.polito.timebanking.models.TimeSlot

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _chatMessageList = MutableLiveData<List<ChatMessage>>()
    val chatMessageList: LiveData<List<ChatMessage>> = _chatMessageList

    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> = _chatList

    var tsid: String? = null
    var uid: String? = null

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    private var chat: Chat? = null
    private var timeSlot: TimeSlot? = null

    fun loadMessages() {
        if (uid == "") {
            uid = auth.currentUser?.uid
        }

        // load chat info
        db.collection("chat")
            .document("$tsid$uid")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    chat = v?.toObject(Chat::class.java)
                }
            }

        // load messages
        db.collection("chat")
            .document("$tsid$uid")
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _chatMessageList.value = v?.mapNotNull { m ->
                        m.toObject(ChatMessage::class.java)
                    }
                } else {
                    _chatMessageList.value = emptyList()
                }
            }

        // load the timeSlot
        tsid?.let {
            db.collection("timeslot")
                .document(it)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        timeSlot = v?.toObject(TimeSlot::class.java)
                    }
                }
        }
    }

    fun loadChats() {
        val uid = auth.currentUser?.uid ?: ""

        db.collection("chat")
            .whereArrayContains("uids", uid)
            .orderBy("timestamp")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _chatList.value = v?.mapNotNull { it.toObject(Chat::class.java) }
                } else {
                    Log.d("AAAAAA", e.toString())
                    _chatList.value = emptyList()
                }
            }
    }

    fun sendMessage(text: String, isChatOpened: Boolean) {
        val currentUid = auth.currentUser?.uid ?: ""
        val timestamp = System.currentTimeMillis()

        val uid: String
        val toUid: String
        if (isChatOpened) {
            uid = chat?.uids?.get(1) ?: ""
            val ownerUid = chat?.uids?.get(0)
            toUid = if (currentUid == ownerUid) {
                uid
            } else {
                ownerUid ?: ""
            }
        } else {
            val ownerId = timeSlot?.uid ?: ""
            uid = this.uid ?: ""
            toUid = ownerId
            chat = Chat("$tsid$currentUid", tsid, listOf(ownerId, currentUid), timestamp)
        }

        chat?.let { oldChat ->
            val newChat = oldChat.copy(timestamp = timestamp)
            db.collection("chat")
                .document("$tsid$uid")
                .set(newChat)
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }

        val message = ChatMessage(currentUid, toUid, text, timestamp)
        db.collection("chat")
            .document("$tsid$uid")
            .collection("messages")
            .document()
            .set(message)
            .addOnSuccessListener { Log.d("Firebase", "Success") }
            .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }

        if (!isChatOpened) {
            loadMessages()
        }
    }

    fun getLoggedUserId(): String? = auth.currentUser?.uid

    fun isChatMine(): Boolean {
        return chat?.uids?.get(0) == auth.currentUser?.uid
    }
}