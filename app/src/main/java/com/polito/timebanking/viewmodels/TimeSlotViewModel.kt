package com.polito.timebanking.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.polito.timebanking.models.TimeSlot

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    var timeslot = TimeSlot()
}