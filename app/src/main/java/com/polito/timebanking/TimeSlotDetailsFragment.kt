package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.viewmodels.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_detail) {

    private lateinit var binding: FragmentTimeSlotDetailBinding
    private val viewModel by viewModels<TimeSlotViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)
        viewModel.timeslot = TimeSlot("Title", "this is a description", 2020, 5, 14, 14, 12, "Duration", "Via delle locheiscion, diesci")
        binding.timeSlot = viewModel.timeslot

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        return binding.root
    }
}