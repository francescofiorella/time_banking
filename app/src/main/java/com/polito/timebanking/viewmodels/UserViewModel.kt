package com.polito.timebanking.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.polito.timebanking.models.Skill
import com.polito.timebanking.models.SkillRepository
import com.polito.timebanking.models.User
import com.polito.timebanking.models.UserRepository
import com.polito.timebanking.models.UserSkill
import com.polito.timebanking.models.UserSkillRepository
import kotlin.concurrent.thread

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userRepository = UserRepository(application)
    private val skillRepository = SkillRepository(application)
    private val userSkillRepository = UserSkillRepository(application)

    val currentUser: LiveData<User?> = userRepository.getUser(1)
    val currentUserSkills: LiveData<List<Skill>> = userSkillRepository.getUserSkills(1)
    val currentUserBitmap = MutableLiveData<Bitmap>()
    val currentUserCheckedSkills = MutableLiveData<MutableList<Skill>>()
    val allSkills: LiveData<List<Skill>> = skillRepository.getSkills()

    fun updateUser(user: User) {
        thread {
            userRepository.updateUser(user)
        }
    }

    fun insertUserSkill(userSkill: UserSkill) {
        thread {
            userSkillRepository.insertUserSkill(userSkill)
        }
    }

    fun deleteUserSkill(userSkill: UserSkill) {
        thread {
            userSkillRepository.deleteUserSkill(userSkill)
        }
    }
}