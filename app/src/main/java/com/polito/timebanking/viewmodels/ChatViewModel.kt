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

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _chatMessageList = MutableLiveData<List<ChatMessage>>()
    val chatMessageList: LiveData<List<ChatMessage>> = _chatMessageList

    var tsid: String? = null

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    private var chat: Chat? = null

    fun loadMessages() {
        val uid = auth.currentUser?.uid

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

        db.collection("chat")
            .document("$tsid$uid")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    chat = v?.toObject(Chat::class.java)
                }
            }

        /*tsid?.let {
            db.collection("timeslot")
                .document(it)
                .addSnapshotListener { v, e ->
                    if (e == null) {
                        timeSlot = v?.toObject(TimeSlot::class.java)
                    }
                }
        }*/
    }

    fun sendMessage(text: String) {
        val currentUid = auth.currentUser?.uid
        val uid = chat?.uid
        val ownerUid = chat?.owner
        val toUid = if (currentUid == ownerUid) {
            uid
        } else {
            ownerUid
        }
        val timestamp = System.currentTimeMillis()
        val message = ChatMessage(currentUid, toUid, text, timestamp)
        db.collection("chat")
            .document("$tsid$uid")
            .collection("messages")
            .document()
            .set(message)
            .addOnSuccessListener { Log.d("Firebase", "Success") }
            .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
    }

    fun getLoggedUserId(): String? = auth.currentUser?.uid
}