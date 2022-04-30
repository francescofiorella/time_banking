package com.polito.timebanking.viewmodels

import android.app.Application
import android.os.Bundle
import androidx.lifecycle.AndroidViewModel
import com.polito.timebanking.models.TimeSlot

class TimeSlotViewModel(application: Application) : AndroidViewModel(application) {
    var timeslot = TimeSlot()

    fun setTimeSlot(bundle: Bundle) {
        timeslot = TimeSlot(
            title = bundle.getString("tstitle"),
            description = bundle.getString("tsdescription"),
            year = bundle.getInt("tsyear"),
            month = bundle.getInt("tsmonth"),
            day = bundle.getInt("tsday"),
            hour = bundle.getInt("tshour"),
            minute = bundle.getInt("tsminute"),
            duration = bundle.getString("tsduration"),
            location = bundle.getString("tslocation")
        )
    }
}