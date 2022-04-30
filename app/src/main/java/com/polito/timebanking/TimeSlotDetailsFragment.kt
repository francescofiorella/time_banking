package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.viewmodels.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTimeSlotDetailBinding
    private val viewModel by viewModels<TimeSlotViewModel>()

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)

        requireArguments().getString("timeslot_key").also {
            if (!it.isNullOrEmpty()) {
                viewModel.timeslot = Gson().fromJson(it, TimeSlot::class.java)
            }
        }
        binding.timeSlot = viewModel.timeslot

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        return binding.root
    }


}