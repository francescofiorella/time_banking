package com.polito.timebanking.models

import android.app.Application
import androidx.lifecycle.LiveData

class TimeSlotRepository(application: Application) {
    private val timeSlotDao = TimeBankingDatabase.getDatabase(application).timeSlotDao()

    fun add(timeSlot: TimeSlot) {
        timeSlotDao.addTimeSlot(timeSlot)
    }

    fun timeSlots(): LiveData<List<TimeSlot>> = timeSlotDao.findAll()

    fun editTimeSlot(timeSlot: TimeSlot) {
        timeSlotDao.updateTimeSlot(timeSlot)
    }
}