package com.polito.timebanking.models

import android.app.Application
import androidx.lifecycle.LiveData

class UserSkillRepository(application: Application) {
    private val userSkillDao = TimeBankingDatabase.getDatabase(application).userSkillDao()

    fun getUserSkills(userId: Long): LiveData<List<Skill>> = userSkillDao.getUserSkills(userId)

    fun insertUserSkill(userSkill: UserSkill) {
        userSkillDao.insertUserSkill(userSkill)
    }

    fun deleteUserSkill(userSkill: UserSkill) {
        userSkillDao.deleteUserSkill(userSkill)
    }
}