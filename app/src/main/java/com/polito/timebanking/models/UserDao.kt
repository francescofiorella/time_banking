package com.polito.timebanking.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM User WHERE id = :id")
    fun getUser(id: Long): LiveData<User>

    @Update
    fun updateUser(user: User)
}