package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.viewmodels.TimeSlotViewModel

class TimeSlotEditFragment: Fragment() {

    private lateinit var binding: FragmentTimeSlotEditBinding
    // custom date and time pickers
    private lateinit var datePickerBtn: DatePickerButton
    private lateinit var timePickerButton: TimePickerButton

    private val viewModel by viewModels<TimeSlotViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_edit, container, false)
        binding.timeSlot = viewModel.timeslot

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init datePicker
        datePickerBtn = object : DatePickerButton(
            binding.dateTextInputLayout,
            binding.dateAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                viewModel.timeslot.year = year!!
                viewModel.timeslot.month = month!!
                viewModel.timeslot.day = day!!
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
                viewModel.timeslot.hour = hour!!
                viewModel.timeslot.minute = minute!!
            }
        }

    }
}