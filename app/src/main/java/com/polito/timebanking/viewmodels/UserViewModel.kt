package com.polito.timebanking.viewmodels

import android.app.Application
import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
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

    private val _user= MutableLiveData<User>()
    val user :LiveData<User> = _user
    val currentUser: LiveData<User?> = userRepository.getUser(1)
    val currentUserSkills: LiveData<List<Skill>> = userSkillRepository.getUserSkills(1)
    val currentUserBitmap = MutableLiveData<Bitmap>()
    val currentUserCheckedSkills = MutableLiveData<MutableList<Skill>>()
    val allSkills: LiveData<List<Skill>> = skillRepository.getSkills()
    private val db: FirebaseFirestore
    private var l: ListenerRegistration

    init{
        db = FirebaseFirestore.getInstance()
        l = db.collection("users").document("1")
            .addSnapshotListener{ v,e ->
                if(e==null){
                    Log.d(TAG, "Current data: ${v?.data}")
                    _user.value = v!!.toUser()
                }else{
                    Log.w(TAG, "Listen failed",e)
                    return@addSnapshotListener
                   // _user.value = emptyList()
                }
            }
    }

    fun create(){
        db
            .collection("users")
            .document()
            .set(mapOf("fullname" to "Speedy","nickname" to "test", "email" to "speedy@it", "location" to "qui", "description" to "ciao"))
            .addOnSuccessListener { Log.d("Firebase","Success") }
            .addOnFailureListener { Log.d("Firebase",it.message?:"Error")}
    }

    fun DocumentSnapshot.toUser():User?{
        return try {
            val id = 1
            val fullname = get("fullname") as String
            val nickname = get("nickname") as String
            val email = get("email") as String
            val location = get("location") as String
            val description = get("description") as String
            val photo = get("photo") as String
            User(id,fullname, nickname,email,location,description,photo)

        } catch (e:Exception){
            e.printStackTrace()
            null
        }

    }

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