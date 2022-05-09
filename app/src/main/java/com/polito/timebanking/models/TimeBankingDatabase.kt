package com.polito.timebanking.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [TimeSlot::class, User::class, Skill::class, UserSkill::class],
    version = 1,
    exportSchema = false
)
abstract class TimeBankingDatabase : RoomDatabase() {
    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun userDao(): UserDao
    abstract fun skillDao(): SkillDao
    abstract fun userSkillDao(): UserSkillDao

    companion object {
        @Volatile
        private var INSTANCE: TimeBankingDatabase? = null

        fun getDatabase(context: Context): TimeBankingDatabase =
            (INSTANCE ?: synchronized(this) {
                val i = INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TimeBankingDatabase::class.java,
                    "timeBanking"
                ).createFromAsset("database/sampleTimeBanking.db").build()
                INSTANCE = i
                INSTANCE
            })!!
    }
}