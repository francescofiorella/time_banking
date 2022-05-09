package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.models.TimeSlotRepository
import kotlin.concurrent.thread

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TimeSlotRepository(application)
    val timeSlotList = repository.timeSlots(1)
    var currentTimeslot: TimeSlot? = null
    var editFragmentMode: Int = NONE

    companion object {
        const val NONE = 0
        const val ADD_MODE = 1
        const val EDIT_MODE = 2
    }

    fun addTimeSlot(timeSlot: TimeSlot) {
        thread {
            repository.add(timeSlot)
        }
    }

    fun updateTimeSlot(timeSlot: TimeSlot) {
        thread {
            repository.editTimeSlot(timeSlot)
        }
    }
}