package com.polito.timebanking.models

import android.app.Application
import androidx.lifecycle.LiveData

class UserRepository(application: Application) {
    private val userDao = TimeBankingDatabase.getDatabase(application).userDao()

    fun getUser(id: Long): LiveData<User> = userDao.getUser(id)

    fun updateUser(user: User) {
        userDao.updateUser(user)
    }
}