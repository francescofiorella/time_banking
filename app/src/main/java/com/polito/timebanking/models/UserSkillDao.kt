package com.polito.timebanking.models

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserSkillDao {
    @Query(
        "SELECT Skill.id AS id, Skill.name AS name FROM Skill " +
                "INNER JOIN UserSkill ON UserSkill.skillId = Skill.id " +
                "WHERE UserSkill.userId = :userId"
    )
    fun getUserSkills(userId: Long): LiveData<List<Skill>>

    @Insert
    fun insertUserSkill(userSkill: UserSkill)

    @Delete
    fun deleteUserSkill(userSkill: UserSkill)
}