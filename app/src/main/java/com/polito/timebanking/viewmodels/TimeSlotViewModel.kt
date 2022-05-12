package com.polito.timebanking.viewmodels

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.models.TimeSlotRepository
import com.polito.timebanking.models.User
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {

    private val _timeslot= MutableLiveData<List<TimeSlot>>()
    val timeslot : LiveData<List<TimeSlot>> = _timeslot

    //private val repository = TimeSlotRepository(application)
    val timeSlotList : LiveData<List<TimeSlot>> = _timeslot
    var currentTimeslot: TimeSlot? = null
    var editFragmentMode: Int = NONE

    var hasBeenModified = false

    companion object {
        const val NONE = 0
        const val ADD_MODE = 1
        const val EDIT_MODE = 2
    }

    private val db: FirebaseFirestore
    private var l: ListenerRegistration

    init{
        db = FirebaseFirestore.getInstance()
        l = db.collection("timeslot")
            .whereEqualTo("user", 1)
            .addSnapshotListener{ v,e ->
                if(e==null){
                    _timeslot.value = v!!.mapNotNull { t -> t.toTimeSlot() }
                }else{
                    _timeslot.value = emptyList()
                }
            }
    }

    fun DocumentSnapshot.toTimeSlot():TimeSlot?{
        return try {
            val id = this.getId() as String
            println("Sono un id >>> "+id);
            val title = get("title") as String
            val day = get("day") as Long
            val month = get("month") as Long
            val year = get("year") as Long
            val duration = get("duration") as String
            val location = get("location") as String
            val description = get("description") as String
            val userId = get("user") as Long
            val hour = get("hour") as Long
            val minute = get("minute") as Long
            TimeSlot(id,title,description, year.toInt(),month.toInt(),day.toInt(),hour.toInt(),minute.toInt(),duration,location,userId.toInt());

        } catch (e:Exception){
            e.printStackTrace()
            null
        }

    }

    fun addTimeSlot(timeSlot: TimeSlot) {
        thread {
            db
                .collection("timeslot")
                .document()
                .set(mapOf("day" to timeSlot.day,"month" to timeSlot.month,"year" to timeSlot.year,"hour" to timeSlot.hour, "minute" to timeSlot.minute,"description" to timeSlot.description, "duration" to timeSlot.duration, "location" to timeSlot.location, "skill" to "franco", "title" to timeSlot.title, "user" to 1))
                .addOnSuccessListener { Log.d("Firebase","Success") }
                .addOnFailureListener { Log.d("Firebase",it.message?:"Error")}
            //repository.add(timeSlot)
        }
    }

    fun updateTimeSlot(timeSlot: TimeSlot) {
        thread {

            //repository.editTimeSlot(timeSlot)
        }
    }
}