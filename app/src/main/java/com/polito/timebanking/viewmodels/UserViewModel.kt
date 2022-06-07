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
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.polito.timebanking.models.*
import com.polito.timebanking.view.profile.ShowProfileFragment.Companion.SHOW_AND_EDIT
import java.io.ByteArrayOutputStream

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage

    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData("")

    private var initialUser: User? = null
    var profileMode = SHOW_AND_EDIT

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _feedbackList = MutableLiveData<List<Feedback?>>()
    val feedbackList: LiveData<List<Feedback?>> = _feedbackList

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    private val _isUserLogged = MutableLiveData<Boolean>()
    val isUserLogged: LiveData<Boolean> = _isUserLogged

    init {
        checkUserLogged()
    }

    private fun checkUserLogged() {
        _isUserLogged.value = auth.currentUser != null
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
                        timeCredit = INITIAL_TIME_CREDIT
                    }
                    setCurrentUser(user, true)
                    checkUserLogged()
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
                checkUserLogged()
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
                            email = email,
                            timeCredit = INITIAL_TIME_CREDIT
                        )
                        setCurrentUser(user, true)
                        checkUserLogged()
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
            .document("${user.uid}")
            .set(user)
            .addOnSuccessListener {
                Log.d(
                    "Firebase/Cloud Firestore",
                    "setCurrentUser: success (uid = ${user.uid})"
                )
                if (isSignIn) {
                    _currentUser.value = user
                }
                checkUserLogged()
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

    fun getUser(uid: String) {
        getCurrentUser(uid, false)
    }


    fun getRatings(uid: String, type: String) {
        var sum = 0.0
        var count = 0

        db.collection("feedback")
            .whereEqualTo(type, uid)
            .addSnapshotListener { v, e ->
                if (e == null) {
                    _feedbackList.value = v!!.mapNotNull { f ->
                        f.toObject(Feedback::class.java).apply {
                            sum += this.rate
                            count++
                        }
                    }

                    if (count == 0) {
                        count = 1
                    }

                    if (type == "writeruid") {
                        db.collection("users")
                            .document(uid)
                            .update(mapOf("givenRatings" to (sum / count)))
                    } else {
                        db.collection("users")
                            .document(uid)
                            .update(mapOf("userRating" to (sum / count)))
                    }
                } else {
                    _feedbackList.value = emptyList()
                }
            }
    }

    fun getCurrentUser(uid: String? = auth.currentUser?.uid, isCurrent: Boolean = true) {
        if (uid.isNullOrEmpty()) {
            _currentUser.value = null
            return
        }
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
                    if (isCurrent) {
                        _currentUser.value = user
                    } else {
                        _user.value = user
                    }
                    if (user.photoUrl.isNullOrEmpty()) {
                        isLoading.value = false
                    }
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
                } else {
                    Log.e(
                        "Firebase/Storage",
                        "setCurrentUserPhoto: failure", task.exception
                    )
                }
            }
    }

    fun signOut() {
        Log.d("UserViewModel", "signOut: success")
        _currentUser.value = null
        auth.signOut()
        checkUserLogged()
    }

    fun setInitialUser() {
        initialUser = _currentUser.value?.copy()
    }

    fun initialUserHasBeenModified(): Boolean {
        return (initialUser != _currentUser.value)
    }

    companion object {
        private const val INITIAL_TIME_CREDIT = 5
    }
}