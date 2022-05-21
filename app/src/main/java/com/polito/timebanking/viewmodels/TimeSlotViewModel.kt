package com.polito.timebanking.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.TimeSlot
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    private val _timeSlotList = MutableLiveData<List<TimeSlot>>()
    val timeSlotList: LiveData<List<TimeSlot>> = _timeSlotList

    var currentTimeslot: TimeSlot? = null
    var editFragmentMode: Int = NONE

    var hasBeenModified = false

    companion object {
        const val NONE = 0
        const val ADD_MODE = 1
        const val EDIT_MODE = 2
    }

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    init {
        db.collection("timeslot")
            .whereEqualTo("email", auth.currentUser?.email ?: "")
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _timeSlotList.value = v!!.mapNotNull { t -> t.toTimeSlot() }
                } else {
                    _timeSlotList.value = emptyList()
                }
            }
    }

    private fun DocumentSnapshot.toTimeSlot(): TimeSlot? {
        return try {
            val id = this.id
            val title = getString("title") ?: ""
            val day = (getLong("day") ?: 0).toInt()
            val month = (getLong("month") ?: 0).toInt()
            val year = (getLong("year") ?: 0).toInt()
            val duration = getString("duration") ?: ""
            val location = getString("location") ?: ""
            val description = getString("description") ?: ""
            val email = getString("email") ?: ""
            val hour = (getLong("hour") ?: 99).toInt()
            val minute = (getLong("minute") ?: 99).toInt()
            val skillId = getString("sid") ?: ""
            TimeSlot(
                id,
                title,
                description,
                year,
                month,
                day,
                hour,
                minute,
                duration,
                location,
                email,
                skillId
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun addTimeSlot(timeSlot: TimeSlot) {
        thread {
            db.collection("timeslot")
                .document()
                .set(
                    mapOf(
                        "day" to timeSlot.day,
                        "month" to timeSlot.month,
                        "year" to timeSlot.year,
                        "hour" to timeSlot.hour,
                        "minute" to timeSlot.minute,
                        "description" to timeSlot.description,
                        "duration" to timeSlot.duration,
                        "location" to timeSlot.location,
                        "sid" to timeSlot.skillId,
                        "title" to timeSlot.title,
                        "email" to timeSlot.email
                    )
                )
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }

    fun updateTimeSlot(timeSlot: TimeSlot) {
        thread {
            db.collection("timeslot")
                .document(timeSlot.id)
                .set(
                    mapOf(
                        "day" to timeSlot.day,
                        "month" to timeSlot.month,
                        "year" to timeSlot.year,
                        "hour" to timeSlot.hour,
                        "minute" to timeSlot.minute,
                        "description" to timeSlot.description,
                        "duration" to timeSlot.duration,
                        "location" to timeSlot.location,
                        "sid" to timeSlot.skillId,
                        "title" to timeSlot.title,
                        "email" to timeSlot.email
                    )
                )
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }
}