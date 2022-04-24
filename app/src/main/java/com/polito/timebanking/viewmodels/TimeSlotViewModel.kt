package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    var year: Int? = null
    var month: Int? = null
    var day: Int? = null
    var hour: Int? = null
    var minute: Int? = null
}