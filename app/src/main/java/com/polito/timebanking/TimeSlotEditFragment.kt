package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding

class TimeSlotEditFragment: Fragment(R.layout.fragment_time_slot_edit) {

    private lateinit var binding: FragmentTimeSlotEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tempBtn.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotEditFragment_to_timeSlotDetailsFragment)
        }
    }
}