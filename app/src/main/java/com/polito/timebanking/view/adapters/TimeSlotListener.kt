package com.polito.timebanking.view.adapters

import com.polito.timebanking.models.TimeSlot

interface TimeSlotListener {
    fun onCardClickListener(timeSlot: TimeSlot, position: Int)
    fun onEditClickListener(timeSlot: TimeSlot, position: Int)
    fun onFeedbackClickListener(timeSlot: TimeSlot, position: Int)
}