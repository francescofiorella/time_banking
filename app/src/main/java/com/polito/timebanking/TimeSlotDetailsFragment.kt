package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_detail) {

    private lateinit var binding: FragmentTimeSlotDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)
        return binding.root
    }
}