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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.polito.timebanking.models.*
import java.io.ByteArrayOutputStream

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    val isLoggedIn = MutableLiveData(false)
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    private var initialUser: User? = null

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _currentUserBitmap = MutableLiveData<Bitmap?>()
    val currentUserBitmap: LiveData<Bitmap?> = _currentUserBitmap

    private val _userBitmap = MutableLiveData<Bitmap?>()
    val userBitmap: LiveData<Bitmap?> = _userBitmap

    init {
        if (auth.currentUser != null) {
            isLoading.value = true
            getCurrentUser(auth.currentUser!!.uid)
        }
    }

    fun getUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    Log.d(
                        "Firebase/Cloud Firestore",
                        "getUser: success (user = ${user})"
                    )
                    _user.value = user
                    user.photoUrl?.let { photoUrl ->
                        getUserPhoto(photoUrl)
                    }
                } else {
                    Log.e(
                        "Firebase/Cloud Firestore",
                        "getUser: failure (user = null)"
                    )
                }
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase/Cloud Firestore",
                    "getUser: failure", it
                )
            }
    }

    private fun getUserPhoto(photoUrl: String) {
        val photoRef = storage.getReferenceFromUrl(photoUrl)
        photoRef
            .getBytes(10 * 1024 * 1024)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                _userBitmap.value = bitmap
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase/Storage",
                    "getCurrentUserPhoto: failure", it
                )
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
                    setCurrentUser(user, true)
                }
            } else {
                Log.e(
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
                    getCurrentUser(currentUser.uid)
                }
            } else {
                Log.e(
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
                        getCurrentUser(uid)
                    } else {
                        val fullName = currentUser.displayName ?: ""
                        val email = currentUser.email ?: ""
                        val user = User(
                            uid = uid,
                            fullName = fullName,
                            email = email
                        )
                        setCurrentUser(user, true)
                    }
                }
            } else {
                Log.e(
                    "Firebase/Authentication",
                    "signInWithCredential: failure", task.exception
                )
                errorMessage.value = "Authentication failed: ${task.exception?.message}"
            }
        }
    }

    fun setCurrentUser(user: User, isSignIn: Boolean) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                Log.d(
                    "Firebase/Cloud Firestore",
                    "setCurrentUser: success (uid = ${user.uid})"
                )
                if (isSignIn) {
                    _currentUser.value = user
                    isLoggedIn.value = true
                }
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase/Cloud Firestore",
                    "setCurrentUser: failure", it
                )
                if (isSignIn) {
                    errorMessage.value = "Registration failed: ${it.message}"
                }
            }
    }

    private fun getCurrentUser(uid: String) {
        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                if (user != null) {
                    Log.d(
                        "Firebase/Cloud Firestore",
                        "getCurrentUser: success (user = ${user})"
                    )
                    _currentUser.value = user
                    user.photoUrl?.let { photoUrl ->
                        getCurrentUserPhoto(photoUrl)
                    }
                    isLoggedIn.value = true
                    isLoading.value = false
                } else {
                    Log.e(
                        "Firebase/Cloud Firestore",
                        "getCurrentUser: failure (user = null)"
                    )
                    errorMessage.value = "Authentication failed: user cannot be retrieve"
                    isLoading.value = false
                }
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase/Cloud Firestore",
                    "getCurrentUser: failure", it
                )
                errorMessage.value = "Authentication failed: ${it.message}"
                isLoading.value = false
            }
    }

    fun setCurrentUserPhoto(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
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
                        "setCurrentUserPhoto: success (downloadUrl = ${task.result})"
                    )
                    currentUser.value?.apply {
                        photoUrl = task.result.toString()
                    }
                    _currentUserBitmap.value = bitmap
                } else {
                    Log.e(
                        "Firebase/Storage",
                        "setCurrentUserPhoto: failure", task.exception
                    )
                }
            }
    }

    private fun getCurrentUserPhoto(photoUrl: String) {
        val photoRef = storage.getReferenceFromUrl(photoUrl)
        photoRef
            .getBytes(10 * 1024 * 1024)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                _currentUserBitmap.value = bitmap
            }
            .addOnFailureListener {
                Log.e(
                    "Firebase/Storage",
                    "getCurrentUserPhoto: failure", it
                )
            }
    }

    fun signOut() {
        Log.d(
            "UserViewModel",
            "signOut: success"
        )
        isLoggedIn.value = false
        _currentUser.value = null
        _currentUserBitmap.value = null
        auth.signOut()
    }

    fun setInitialUser() {
        initialUser = _currentUser.value?.copy()
    }

    fun initialUserHasBeenModified(): Boolean {
        return (initialUser != _currentUser.value)
    }
}