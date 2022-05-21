package com.polito.timebanking.viewmodels

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.google.firebase.storage.ktx.storage
import com.polito.timebanking.models.*
import java.io.ByteArrayOutputStream

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    val loggedIn = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _skills = MutableLiveData<List<Skill>>()
    val skills: LiveData<List<Skill>> = _skills

    private val _photoBitmap = MutableLiveData<Bitmap?>()
    val photoBitmap: LiveData<Bitmap?> = _photoBitmap

    private var skillsListener: ListenerRegistration

    init {
        if (auth.currentUser != null) {
            getUser(auth.currentUser!!.uid)
        }

        skillsListener = db.collection("skills")
            .addSnapshotListener { value, e ->
                if (e != null) {
                    Log.w(
                        "Firebase/Cloud Firestore",
                        "Skills Listener: failure", e
                    )
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
                Log.d(
                    "Firebase/Cloud Firestore",
                    "Skills Listener: success (skills = ${skills})"
                )
                _skills.value = skills
            }
    }

    fun signUpWithEmailAndPassword(email: String, password: String, user: User) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(
                    "Firebase/Authentication",
                    "signUpWithEmailAndPassword: success"
                )
                auth.currentUser?.also { currentUser ->
                    user.apply {
                        uid = currentUser.uid
                    }
                    setUser(user, true)
                }
            } else {
                Log.w(
                    "Firebase/Authentication",
                    "signUpWithEmailAndPassword: failure", task.exception
                )
                errorMessage.value = "Registration failed: ${task.exception?.message}"
            }
        }
    }

    fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(
                    "Firebase/Authentication",
                    "signInWithEmailAndPassword: success"
                )
                auth.currentUser?.also { currentUser ->
                    getUser(currentUser.uid)
                }
            } else {
                Log.w(
                    "Firebase/Authentication",
                    "signInWithEmailAndPassword: failure", task.exception
                )
                errorMessage.value = "Authentication failed: ${task.exception?.message}"
            }
        }
    }

    fun signInWithCredential(firebaseCredential: AuthCredential) {
        auth.signInWithCredential(firebaseCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(
                    "Firebase/Authentication",
                    "signInWithCredential: success"
                )
                val isNewUser = task.result.additionalUserInfo?.isNewUser
                auth.currentUser?.also { currentUser ->
                    val uid = currentUser.uid
                    if (isNewUser == false) {
                        getUser(uid)
                    } else {
                        val fullName = currentUser.displayName ?: ""
                        val email = currentUser.email ?: ""
                        val user = User(
                            uid = uid,
                            fullName = fullName,
                            email = email
                        )
                        setUser(user, true)
                    }
                }
            } else {
                Log.w(
                    "Firebase/Authentication",
                    "signInWithCredential: failure", task.exception
                )
                errorMessage.value = "Authentication failed: ${task.exception?.message}"
            }
        }
    }

    fun setUser(user: User, isSignIn: Boolean) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(
                    "Firebase/Cloud Firestore",
                    "setUser: success (uid = ${user.uid})"
                )
                if (isSignIn) {
                    _currentUser.value = user
                    loggedIn.value = true
                }
            }
            .addOnFailureListener {
                Log.w(
                    "Firebase/Cloud Firestore",
                    "setUser: failure", it
                )
                if (isSignIn) {
                    errorMessage.value = "Registration failed: ${it.message}"
                }
            }
    }

    private fun getUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                Log.d(
                    "Firebase/Cloud Firestore",
                    "getUser: success (user = ${user})"
                )
                _currentUser.value = user
                user?.photoUrl?.let { photoUrl ->
                    getPhoto(photoUrl)
                }
                loggedIn.value = true
            }
            .addOnFailureListener {
                Log.w(
                    "Firebase/Cloud Firestore",
                    "getUser: failure", it
                )
                errorMessage.value = "Authentication failed: ${it.message}"
            }
    }

    fun setPhoto(photoBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val photoRef = storage.reference.child("userProfilePhotos/${currentUser.value!!.uid}.jpg")
        photoRef
            .putBytes(baos.toByteArray())
            .continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                photoRef.downloadUrl
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(
                        "Firebase/Storage",
                        "setPhoto: success (downloadUrl = ${task.result})"
                    )
                    currentUser.value?.apply {
                        photoUrl = task.result.toString()
                    }
                    _photoBitmap.value = photoBitmap
                } else {
                    Log.w(
                        "Firebase/Storage",
                        "setPhoto: failure", task.exception
                    )
                }
            }
    }

    private fun getPhoto(photoUrl: String) {
        val photoRef = storage.getReferenceFromUrl(photoUrl)
        photoRef
            .getBytes(10 * 1024 * 1024)
            .addOnSuccessListener {
                val photoBitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                _photoBitmap.value = photoBitmap
            }
            .addOnFailureListener {
                Log.w(
                    "Firebase/Storage",
                    "getPhoto: failure", it
                )
            }
    }

    fun signOut() {
        Log.d(
            "UserViewModel",
            "signOut: success"
        )
        loggedIn.value = false
        _currentUser.value = null
        _photoBitmap.value = null
        auth.signOut()
    }

    fun clearErrorMessage() {
        errorMessage.value = ""
    }

    override fun onCleared() {
        super.onCleared()
        skillsListener.remove()
    }
}