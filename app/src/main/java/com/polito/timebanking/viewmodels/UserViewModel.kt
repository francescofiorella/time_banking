package com.polito.timebanking.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    val signInErrorMessage = MutableLiveData("")
    val signUpErrorMessage = MutableLiveData("")

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
                        Log.d("Firebase", "Skills Listener: success (skill = ${it})")
                        if (it.sid != null && it.name != null) {
                            skills.add(it)
                        }
                    }
                }
                Log.d("Firebase", "Skills Listener: success (skills = ${skills})")
                _skills.value = skills
            }
    }

    fun createUserWithEmailAndPassword(email: String, password: String, user: User) {
        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "createUserWithEmailAndPassword: success")
                fAuth.currentUser?.also { currentUser ->
                    user.apply {
                        uid = currentUser.uid
                    }
                    addUser(currentUser.uid, user)
                    loggedIn.value = true
                }
            } else {
                Log.w("Firebase", "createUserWithEmailAndPassword: failure", task.exception)
                signUpErrorMessage.value = "Registration failed: ${task.exception?.message}"
            }
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
                signUpErrorMessage.value = "Registration failed: ${it.message}"
            }
    }

    private fun getUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                Log.d("Firebase", "getUser: success")
                _currentUser.value = it.toObject(User::class.java)
            }
            .addOnFailureListener {
                Log.w("Firebase", "getUser: failure", it)
                signInErrorMessage.value = "Authentication failed: ${it.message}"
            }
    }

    fun signOut() {
        Log.d("DEBUG", "UserViewModel - signOut")
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

    fun clearSignInErrorMessage() {
        signInErrorMessage.value = ""
    }

    fun clearSignUpErrorMessage() {
        signUpErrorMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        skillsListener.remove()
    }
}