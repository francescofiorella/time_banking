package com.polito.timebanking.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.*

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val fAuth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    val loggedIn = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _skills = MutableLiveData<List<Skill>>()
    val skills: LiveData<List<Skill>> = _skills

    private val _currentUserBitmap = MutableLiveData<Bitmap>()
    val currentUserBitmap: LiveData<Bitmap> = _currentUserBitmap

    private var skillsListener: ListenerRegistration

    init {
        if (fAuth.currentUser != null) {
            getUser(fAuth.currentUser!!.uid)
            loggedIn.value = true
        }

        skillsListener = db.collection("skills")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w("Firebase", "Skills Listener: failure", e)
                    return@addSnapshotListener
                }
                val skills = ArrayList<Skill>()
                for (skill in value!!) {
                    skill.toObject(Skill::class.java).let {
                        if (it.sid != null && it.name != null) {
                            skills.add(it)
                        }
                    }
                }
                Log.d("Firebase", "Skills Listener: success (skills = ${skills})")
                _skills.value = skills
            }
    }

    fun signInWithCredential(firebaseCredential: AuthCredential) {
        fAuth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "signInWithCredential: success")
                val isNewUser = task.result.additionalUserInfo?.isNewUser
                fAuth.currentUser?.also { currentUser ->
                    val uid = currentUser.uid
                    if (isNewUser == false) {
                        getUser(uid)
                        loggedIn.value = true
                    } else {
                        val fullName = currentUser.displayName ?: ""
                        val email = currentUser.email ?: ""
                        val photoUrl = currentUser.photoUrl
                        Log.d(
                            "DEBUG",
                            "isNewUser = $isNewUser, uid = $uid, fullName = $fullName, email = $email, photoUrl = $photoUrl"
                        )
                        val user = User(
                            uid = uid,
                            fullName = fullName,
                            email = email
                        )
                        addUser(uid, user)
                        loggedIn.value = true
                    }
                }
            } else {
                Log.w("Firebase", "signInWithCredential: failure", task.exception)
                errorMessage.value = "Authentication failed: ${task.exception?.message}"
            }
        }
    }

    private fun addUser(uid: String, user: User) {
        db.collection("users")
            .document(uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "addUser: success (id = ${uid})")
                _currentUser.value = user
            }
            .addOnFailureListener {
                Log.w("Firebase", "addUser: failure", it)
                errorMessage.value = "Registration failed: ${it.message}"
            }
    }

    private fun getUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                Log.d("Firebase", "getUser: success (user = ${user})")
                _currentUser.value = user
            }
            .addOnFailureListener {
                Log.w("Firebase", "getUser: failure", it)
                errorMessage.value = "Authentication failed: ${it.message}"
            }
    }

    fun signOut() {
        Log.d("UserViewModel", "signOut")
        loggedIn.value = false
        _currentUser.value = null
        fAuth.signOut()
    }

    fun updateUser(user: User) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d("Firebase", "updateUser: success")
            }
            .addOnFailureListener {
                Log.w("Firebase", "updateUser: failure", it)
            }
    }

    fun clearErrorMessage() {
        errorMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        skillsListener.remove()
    }
}