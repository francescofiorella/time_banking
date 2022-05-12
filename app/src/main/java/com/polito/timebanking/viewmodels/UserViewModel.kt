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
import com.polito.timebanking.models.*
import kotlin.concurrent.thread

class UserViewModel(application: Application) : AndroidViewModel(application) {
    //private val userRepository = UserRepository(application)
    //private val userSkillRepository = UserSkillRepository(application)

    private val skillRepository = SkillRepository(application)



    private val _user= MutableLiveData<User>()
    val user :LiveData<User> = _user
    val currentUser: LiveData<User?> = _user

    private val _skill= MutableLiveData<List<Skill>>()
    val skill : LiveData<List<Skill>> = _skill
    val currentUserSkills: LiveData<List<Skill>> = _skill

    val currentUserBitmap = MutableLiveData<Bitmap>()
    val currentUserCheckedSkills = MutableLiveData<MutableList<Skill>>()

    private val _allskill= MutableLiveData<List<Skill>>()
    val allSkills: LiveData<List<Skill>> = _allskill

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
        l= db.collection("skills").document("DefaultSkills").addSnapshotListener{
            v,e->
            if(e==null){
                val skills= v?.get("skill") as ArrayList<String>
                _allskill.value = skills!!.mapNotNull { s -> Skill(s.indexOf(s),s) }
            }
        }
    }

    fun createUser(){
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
            val skills = get("skills") as ArrayList<String>
            _skill.value = skills!!.mapNotNull {s -> Skill(s.indexOf(s),s)}
            User(id,fullname, nickname,email,location,description,photo)
        } catch (e:Exception){
            e.printStackTrace()
            null
        }
    }






    fun updateUser(user: User) {
        println(currentUserSkills.value)
        thread {
            db
                .collection("users")
                .document(user.id.toString())
                .set(mapOf("description" to user.description,"email" to user.email,"nickname" to user.nickname,"fullname" to user.fullName, "location" to user.location,"photo" to user.photoPath, "skills" to (currentUserSkills.value?.map{ it.name}) ))
                .addOnSuccessListener { Log.d("Firebase","Success") }
                .addOnFailureListener { Log.d("Firebase",it.message?:"Error")}
            //userRepository.updateUser(user)
        }
    }

    fun insertUserSkill(userSkill: UserSkill) {
        thread {
            //userSkillRepository.insertUserSkill(userSkill)
        }
    }

    fun deleteUserSkill(userSkill: UserSkill) {
        thread {
            //userSkillRepository.deleteUserSkill(userSkill)
        }
    }
}