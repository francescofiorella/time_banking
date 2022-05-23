package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.polito.timebanking.models.Skill

class SkillViewModel(application: Application) : AndroidViewModel(application) {
    private val _skillList = MutableLiveData<List<Skill>>()
    val skillList: LiveData<List<Skill>> = _skillList

    private var skillListListener: ListenerRegistration

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    init {
        skillListListener = db.collection("skills")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _skillList.value = v?.mapNotNull { s -> s.toObject(Skill::class.java) }
                } else {
                    _skillList.value = emptyList()
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        skillListListener.remove()
    }
}