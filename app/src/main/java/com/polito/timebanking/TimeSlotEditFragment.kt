package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE

class TimeSlotEditFragment: Fragment() {

    private lateinit var binding: FragmentTimeSlotEditBinding
    // custom date and time pickers
    private lateinit var datePickerBtn: DatePickerButton
    private lateinit var timePickerButton: TimePickerButton

    private val viewModel by activityViewModels<TimeSlotViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_edit, container, false)

        /*if (savedInstanceState == null) {
            // se proviene da listFragment
            requireArguments().getString(TIMESLOT_KEY).also {
                if (!it.isNullOrEmpty()) {
                    requireArguments().remove(TIMESLOT_KEY)
                    viewModel.currentTimeslot = Gson().fromJson(it, TimeSlot::class.java)
                }
            }
        }*/
        binding.timeSlot = viewModel.currentTimeslot

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
                viewModel.currentTimeslot?.year = year!!
                viewModel.currentTimeslot?.month = month!!
                viewModel.currentTimeslot?.day = day!!
            }
        }

        datePickerBtn.year = viewModel.currentTimeslot?.year.takeIf { it != 0 }
        datePickerBtn.month = viewModel.currentTimeslot?.month.takeIf { it != 0 }
        datePickerBtn.day = viewModel.currentTimeslot?.day.takeIf { it != 0 }

        // init timePicker
        timePickerButton = object : TimePickerButton(
            binding.timeTextInputLayout,
            binding.timeAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                viewModel.currentTimeslot?.hour = hour!!
                viewModel.currentTimeslot?.minute = minute!!
            }
        }

        timePickerButton.hour = viewModel.currentTimeslot?.hour.takeIf { it != 99 }
        timePickerButton.minute = viewModel.currentTimeslot?.minute.takeIf { it != 99 }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.currentTimeslot?.let { timeSlot ->
            timeSlot.title = binding.titleEt.text.toString()
            timeSlot.description = binding.descriptionEt.text.toString()
            // date and time are saved on positive button click
            timeSlot.duration = binding.durationEt.text.toString()
            timeSlot.location = binding.locationEt.text.toString()
            // save the list
            when (viewModel.editFragmentMode) {
                ADD_MODE -> viewModel.addTimeSlot(timeSlot)
                EDIT_MODE -> viewModel.updateTimeSlot(timeSlot)
                else -> Unit
            }
        }
    }
}