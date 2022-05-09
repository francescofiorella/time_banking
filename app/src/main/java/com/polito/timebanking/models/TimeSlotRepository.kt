package com.polito.timebanking.models

import android.app.Application
import androidx.lifecycle.LiveData

class TimeSlotRepository(application: Application) {
    private val timeSlotDao = TimeBankingDatabase.getDatabase(application).timeSlotDao()

    fun add(timeSlot: TimeSlot) {
        timeSlotDao.addTimeSlot(timeSlot)
    }

    fun timeSlots(userId: Int): LiveData<List<TimeSlot>> = timeSlotDao.getAllByUserId(userId)

    fun editTimeSlot(timeSlot: TimeSlot) {
        timeSlotDao.updateTimeSlot(timeSlot)
    }
}