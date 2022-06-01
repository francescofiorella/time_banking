package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.ChatMessage

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val _chatMessageList = MutableLiveData<List<ChatMessage>>()
    val chatMessageList: LiveData<List<ChatMessage>> = _chatMessageList

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    fun loadMessages(uid: String, tsid: String) {
        db.collection("chat")
            .document("$tsid$uid")
            .collection("messages")
            .addSnapshotListener{ v, e ->
                if (e == null) {
                    _chatMessageList.value = v?.mapNotNull { m ->
                        m.toObject(ChatMessage::class.java)
                    }
                } else {
                    _chatMessageList.value = emptyList()
                }
            }
    }

    fun getLoggedUserId(): String? = auth.currentUser?.uid
}