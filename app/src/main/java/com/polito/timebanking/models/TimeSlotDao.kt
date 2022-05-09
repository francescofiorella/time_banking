package com.polito.timebanking.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeSlotDao {
    @Query("SELECT * FROM timeSlot WHERE userId = :userId")
    fun getAllByUserId(userId: Int): LiveData<List<TimeSlot>>

    @Insert
    fun addTimeSlot(timeSlot: TimeSlot)

    @Update
    fun updateTimeSlot(timeSlot: TimeSlot)
}