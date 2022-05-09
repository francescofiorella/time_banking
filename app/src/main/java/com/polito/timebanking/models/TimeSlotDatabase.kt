package com.polito.timebanking.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TimeSlot::class], version = 1)
abstract class TimeSlotDatabase : RoomDatabase() {
    abstract fun timeSlotDao(): TimeSlotDao

    companion object {
        @Volatile
        private var INSTANCE: TimeSlotDatabase? = null

        fun getDatabase(context: Context): TimeSlotDatabase =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TimeSlotDatabase::class.java,
                    "timeSlots"
                ).build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}