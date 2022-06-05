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
import com.polito.timebanking.view.timeslots.TimeSlotListFragment.Companion.MY_LIST
import com.polito.timebanking.view.timeslots.TimeSlotListFragment.Companion.SKILL_LIST
import com.polito.timebanking.models.TimeSlot
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    val timeSlotList: LiveData<List<TimeSlot>> = _timeSlotList

    private var initialTimeSlot: TimeSlot? = null
    var currentTimeSlot: TimeSlot? = null
    var editFragmentMode: Int = NONE

    var listFragmentMode = MY_LIST
    var sid = ""

    val isLoading = MutableLiveData(false)

    companion object {
        const val NONE = 0
        const val ADD_MODE = 1
        const val EDIT_MODE = 2
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    fun loadRequiredList(){
        isLoading.value = true
        val query = db.collection("timeslot")
            .whereEqualTo("cid", auth.currentUser?.uid ?: "")
        query.addSnapshotListener { v, e ->
            if (e == null) {
                _timeSlotList.value = v!!.mapNotNull { t ->
                    t.toObject(TimeSlot::class.java).apply { id = t.id }
                }
            } else {
                _timeSlotList.value = emptyList()
            }
            isLoading.value = false
        }
    }

    fun loadList() {
        isLoading.value = true
        val query = if (listFragmentMode == SKILL_LIST) {
            db.collection("timeslot")
                .whereEqualTo("sid", sid)
        } else {
            db.collection("timeslot")
                .whereEqualTo("uid", auth.currentUser?.uid ?: "")
        }

        query.addSnapshotListener { v, e ->
            if (e == null) {
                _timeSlotList.value = v!!.mapNotNull { t ->
                    t.toObject(TimeSlot::class.java).apply { id = t.id }
                }
            } else {
                _timeSlotList.value = emptyList()
            }
            isLoading.value = false
        }
    }

    fun resetList() {
        _timeSlotList.value = emptyList()
    }

    fun addTimeSlot(timeSlot: TimeSlot) {
        thread {
            timeSlot.uid = auth.currentUser?.uid ?: ""
            db.collection("timeslot")
                .document()
                .set(timeSlot)
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }

    fun updateTimeSlot(timeSlot: TimeSlot) {
        thread {
            db.collection("timeslot")
                .document(timeSlot.id)
                .set(timeSlot)
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }

    fun isCurrentTimeSlotMine(): Boolean = (initialTimeSlot?.uid == auth.currentUser?.uid)

    fun setTimeSlot(timeSlot: TimeSlot?) {
        initialTimeSlot = timeSlot
        currentTimeSlot = timeSlot?.copy()
    }

    fun tsHasBeenModified(): Boolean {
        return (initialTimeSlot != currentTimeSlot)
    }
}