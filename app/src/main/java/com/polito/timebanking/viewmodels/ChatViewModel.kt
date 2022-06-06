package com.polito.timebanking.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.Chat
import com.polito.timebanking.models.ChatMessage
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.models.User

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _chatMessageList = MutableLiveData<List<ChatMessage>>()
    val chatMessageList: LiveData<List<ChatMessage>> = _chatMessageList

    private val _chatList = MutableLiveData<List<Chat>>()
    val chatList: LiveData<List<Chat>> = _chatList

    var tsid: String? = null
    var uid: String? = null

    private val _otherUser = MutableLiveData<User>()
    val otherUser: LiveData<User> = _otherUser

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    private var chat: Chat? = null
    private var _timeSlot = MutableLiveData<TimeSlot>()
    val timeSlot: LiveData<TimeSlot> = _timeSlot

    fun loadMessages() {
        if (uid == "") {
            uid = auth.currentUser?.uid
        }

        // load the timeSlot
        tsid?.let {
            db.collection("timeslot")
                .document(it)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        _timeSlot.value = v?.toObject(TimeSlot::class.java)

                        // load chat info
                        db.collection("chat")
                            .document("$tsid$uid")
                            .addSnapshotListener { va, err ->
                                if (err == null) {
                                    chat = va?.toObject(Chat::class.java)
                                    val otherUid = if (chat != null) {
                                        // load other_user
                                        val currentUid = auth.currentUser?.uid ?: ""
                                        if (currentUid == chat?.uids?.get(0)) {
                                            chat?.uids?.get(1) ?: ""
                                        } else {
                                            chat?.uids?.get(0) ?: ""
                                        }
                                    } else {
                                        // load timeslot owner
                                        _timeSlot.value?.uid ?: ""
                                    }
                                    if (otherUid.isNotEmpty()) {
                                        db.collection("users")
                                            .document(otherUid)
                                            .addSnapshotListener { value, error ->
                                                if (error == null) {
                                                    _otherUser.value =
                                                        value?.toObject(User::class.java)
                                                }
                                            }
                                    }
                                }
                            }
                    }
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
    }

    fun loadChats() {
        val uid = auth.currentUser?.uid ?: ""

        db.collection("chat")
            .whereArrayContains("uids", uid)
            .orderBy("timestamp", Query.Direction.DESCENDING)
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
            val ownerId = _timeSlot.value?.uid ?: ""
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

    fun isTimeSlotAvailable(): Boolean = _timeSlot.value?.state == ""

    fun acceptTimeSlot() {
        _timeSlot.value?.let { timeslot ->
            timeslot.state = "accepted"
            db.collection("timeslot")
                .document(timeslot.id)
                .set(timeslot)
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }
}