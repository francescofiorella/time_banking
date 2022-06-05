package com.polito.timebanking.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.models.TimeSlot
import kotlin.concurrent.thread


class FeedbackViewModel(application: Application) : AndroidViewModel(application) {
    private val _Feedback = MutableLiveData<Feedback>()
    val feedback: LiveData<Feedback> = _Feedback

    var currentFeedback: Feedback? =null

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth: FirebaseAuth = Firebase.auth

    fun getFeedback(timeslot: String){
             db.collection("feedback")
                 .document(timeslot)
                 .get()
                 .addOnSuccessListener {
                     val feedback = it.toObject(Feedback::class.java)
                     if(feedback!=null){
                         Log.d("Firebase/Cloud Firestore",
                             "getFeedback: success (feedback = ${feedback})"  )
                         _Feedback.value = feedback!!
                     }else{
                         Log.e("Firebase/Cloud Firestore",
                             "getFeedback: failure (feedback = null)")
                     }
                 }
                 .addOnFailureListener{
                     Log.e(
                         "Firebase/Cloud Firestore",
                         "getCurrentUser: failure", it
                     )
                 }
    }

    fun addFeedback(feedback: Feedback){
        thread{
            feedback.writeruid = auth.currentUser?.uid?: ""
            db.collection("feedback")
                .document()
                .set(feedback)
                .addOnSuccessListener { Log.d("Firebase", "Success") }
                .addOnFailureListener { Log.d("Firebase", it.message ?: "Error") }
        }
    }

    fun setFeedback(feedback: Feedback) {
        currentFeedback = feedback
    }
}