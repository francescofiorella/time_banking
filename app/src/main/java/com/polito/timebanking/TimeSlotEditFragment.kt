package com.polito.timebanking

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class TimeSlotEditFragment: Fragment(R.layout.fragment_time_slot_edit) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btn = view.findViewById<Button>(R.id.btn)
        btn.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment)
        }
    }
}