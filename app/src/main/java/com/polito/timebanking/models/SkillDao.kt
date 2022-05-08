package com.polito.timebanking.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface SkillDao {
    @Query("SELECT * FROM Skill")
    fun getSkills(): LiveData<List<Skill>>
}