package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.polito.timebanking.models.TimeSlot

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val NONE = 0
        const val ADD_MODE = 1
        const val EDIT_MODE = 2
    }

    // TODO: implements a local database with room
    val timeSlotList = mutableListOf(
        TimeSlot(title = "Time Slot 1", location = "Location 1", year = 2020, month = 10, day = 12),
        TimeSlot(title = "Time Slot 2", location = "Location 2", year = 2020, month = 9, day = 14),
        TimeSlot(title = "Time Slot 3", location = "Location 3", year = 2020, month = 8, day = 30),
        TimeSlot(title = "Time Slot 4", location = "Location 4", year = 2020, month = 10, day = 4)
    )

    var currentTimeslot : TimeSlot? = null

    var editFragmentMode : Int = NONE
}