package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.viewmodels.TimeSlotViewModel

class TimeSlotEditFragment: Fragment(R.layout.fragment_time_slot_edit) {

    private lateinit var binding: FragmentTimeSlotEditBinding
    // custom date and time pickers
    private lateinit var datePickerBtn: DatePickerButton
    private lateinit var timePickerButton: TimePickerButton

    val viewModel by viewModels<TimeSlotViewModel>()

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

        // init datePicker
        datePickerBtn = object : DatePickerButton(
            binding.dateTextInputLayout,
            binding.dateAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                viewModel.year = year
                viewModel.month = month
                viewModel.day = day
            }
        }

        // init timePicker
        timePickerButton = object : TimePickerButton(
            binding.timeTextInputLayout,
            binding.timeAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                viewModel.hour = hour
                viewModel.minute = minute
            }
        }
    }
}