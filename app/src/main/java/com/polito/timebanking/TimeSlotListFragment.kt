package com.polito.timebanking

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TimeSlotListFragment: Fragment(R.layout.fragment_time_slot_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }

    }
}