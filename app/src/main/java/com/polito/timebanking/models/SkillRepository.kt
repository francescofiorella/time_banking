package com.polito.timebanking.models

import android.app.Application
import androidx.lifecycle.LiveData

class SkillRepository(application: Application) {
    private val skillDao = UsersDatabase.getDatabase(application).skillDao()

    fun getSkills(): LiveData<List<Skill>> = skillDao.getSkills()
}