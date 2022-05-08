package com.polito.timebanking.models

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, Skill::class, UserSkill::class],
    version = 1,
    exportSchema = false
)
abstract class UsersDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun skillDao(): SkillDao
    abstract fun userSkillDao(): UserSkillDao

    companion object {
        @Volatile
        private var INSTANCE: UsersDatabase? = null

        fun getDatabase(context: Context): UsersDatabase =
            (
                INSTANCE ?: synchronized(this) {
                    val i = INSTANCE ?: Room
                        .databaseBuilder(
                            context.applicationContext,
                            UsersDatabase::class.java,
                            "users"
                        )
                        .createFromAsset("database/sampleUsers.db")
                        .build()
                    INSTANCE = i
                    INSTANCE
                }
            )!!
    }
}