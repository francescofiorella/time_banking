package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.NONE

class TimeSlotDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTimeSlotDetailBinding
    private val viewModel by activityViewModels<TimeSlotViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)

        binding.timeSlot = viewModel.currentTimeSlot

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        viewModel.hasBeenModified = false
        viewModel.editFragmentMode = NONE
    }

    fun navigateToEdit() {
        viewModel.hasBeenModified = false
        viewModel.editFragmentMode = EDIT_MODE
        findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment)
    }
}