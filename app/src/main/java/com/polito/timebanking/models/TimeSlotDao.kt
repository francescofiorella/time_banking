package com.polito.timebanking.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TimeSlotDao {
    @Query("SELECT * FROM timeSlots")
    fun findAll(): LiveData<List<TimeSlot>>

    @Query("SELECT count() FROM timeSlots")
    fun count(): LiveData<Int>

    @Insert
    fun addTimeSlot(timeSlot: TimeSlot)

    @Query("DELETE FROM timeSlots")
    fun removeAll()

    @Update
    fun updateTimeSlot(timeSlot: TimeSlot)
}