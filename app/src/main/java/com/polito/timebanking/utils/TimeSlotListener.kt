package com.polito.timebanking.utils

import com.polito.timebanking.models.TimeSlot

interface TimeSlotListener {
    fun onCardClickListener(timeSlot: TimeSlot, position: Int)
    fun onEditClickListener(timeSlot: TimeSlot, position: Int)
}