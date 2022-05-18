package com.polito.timebanking.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.*
import kotlin.concurrent.thread

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val fAuth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val loggedIn = MutableLiveData(false)
    val signInErrorMessage = MutableLiveData("")
    val signUpErrorMessage = MutableLiveData("")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _skill = MutableLiveData<List<Skill>>()
    val skill: LiveData<List<Skill>> = _skill
    val currentUserSkills: LiveData<List<Skill>> = _skill

    val currentUserBitmap = MutableLiveData<Bitmap>()
    val currentUserCheckedSkills = MutableLiveData<MutableList<Skill>>()

    private val _allSkills = MutableLiveData<List<Skill>>()
    val allSkills: LiveData<List<Skill>> = _allSkills

//    private var lUser: ListenerRegistration
//    private var lSkills: ListenerRegistration

    init {
        if (fAuth.currentUser != null) {
            getUser(fAuth.currentUser!!.uid)
            loggedIn.value = true
        }
//        lUser = db.collection("users")
//            .document("1")
//            .addSnapshotListener { v, e ->
//                if (e == null) {
//                    Log.d(TAG, "Current data: ${v?.data}")
//                    _user.value = v!!.toUser()
//
//                } else {
//                    Log.w(TAG, "Listen failed", e)
//                    return@addSnapshotListener
//                    // _user.value = emptyList()
//                }
//            }
//        lSkills = db.collection("skills").document("DefaultSkills").addSnapshotListener { v, e ->
//            if (e == null) {
//                val skills = v?.get("skill") as ArrayList<String>
//                _allSkills.value = skills!!.mapNotNull { s -> Skill(s.indexOf(s), s) }
//            }
//        }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, user: User) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "createUserWithEmailAndPassword: success")
                fAuth.currentUser?.also { currentUser ->
                    user.apply {
                        uid = currentUser.uid
                    }
                    storeUser(user)
                    loggedIn.value = true
                }
            } else {
                Log.w("Firebase", "createUserWithEmailAndPassword: failure", task.exception)
                signUpErrorMessage.value = "Registration failed: ${task.exception?.message}"
            }
        }
    }

    private fun storeUser(user: User) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "storeUser: success")
                _currentUser.value = user
            }
            .addOnFailureListener {
                Log.w("Firebase", "storeUser: failure", it)
                signUpErrorMessage.value = "Registration failed: ${it.message}"
            }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "signInWithEmailAndPassword: success")
                fAuth.currentUser?.also { currentUser ->
                    getUser(currentUser.uid)
                    loggedIn.value = true
                }
            } else {
                Log.w("Firebase", "signInWithEmailAndPassword: failure", task.exception)
                signInErrorMessage.value = "Authentication failed: ${task.exception?.message}"
            }
        }
    }

    private fun getUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                Log.d("Firebase", "getUser: success")
                Log.d("DEBUG", "UserViewModel - getUser - ${it.toUser()}")
                _currentUser.value = it.toUser()
            }
            .addOnFailureListener {
                Log.w("Firebase", "getUser: failure", it)
                signInErrorMessage.value = "Authentication failed: ${it.message}"
            }
    }

    private fun DocumentSnapshot.toUser(): User? {
        return try {
            val uid = get("uid") as String
            val fullName = get("fullName") as String
            val nickname = get("nickname") as String
            val email = get("email") as String
            val location = get("location") as String?
            val description = get("description") as String?
            val photoPath = get("photoPath") as String?
            User(uid, fullName, nickname, email, location, description, photoPath)
        } catch (e: Exception) {
            Log.w("DocumentSnapshot", "toUser: failure", e)
            null
        }
    }

    fun signOut() {
        Log.d("DEBUG", "UserViewModel - signOut")
        loggedIn.value = false
        _currentUser.value = null
        fAuth.signOut()
    }

    fun updateUser(user: User) {
        println(currentUserSkills.value)
        thread {
//            db
//                .collection("users")
//                .document(user.id.toString())
//                .set(
//                    mapOf(
//                        "description" to user.description,
//                        "email" to user.email,
//                        "nickname" to user.nickname,
//                        "fullname" to user.fullName,
//                        "location" to user.location,
//                        "photo" to user.photoPath,
//                        "skills" to (currentUserSkills.value?.map { it.name })
//                    )
//                )
//                .addOnSuccessListener { Log.d("Firebase", "Success") }
//                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
//            //userRepository.updateUser(user)
        }
    }

//    fun insertUserSkill(userSkill: UserSkill) {
//        thread {
//            //userSkillRepository.insertUserSkill(userSkill)
//        }
//    }
//
//    fun deleteUserSkill(userSkill: UserSkill) {
//        thread {
//            //userSkillRepository.deleteUserSkill(userSkill)
//        }
//    }

    override fun onCleared() {
        super.onCleared()
//        lUser.remove()
//        lSkills.remove()
    }

    fun clearSignInErrorMessage() {
        signInErrorMessage.value = ""
    }

    fun clearSignUpErrorMessage() {
        signUpErrorMessage.value = ""
    }
}