package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE

class TimeSlotEditFragment : Fragment() {

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

        binding.timeSlot = viewModel.currentTimeslot

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        // save timeSlot onBackPressed
        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.currentTimeslot?.let { timeSlot ->
                        val title = binding.titleEt.text.toString()
                        val description = binding.descriptionEt.text.toString()
                        val duration = binding.durationEt.text.toString()
                        val location = binding.locationEt.text.toString()

                        if (timeSlot.title != title
                            || timeSlot.description != description
                            || timeSlot.duration != duration
                            || timeSlot.location != location
                        ) {
                            viewModel.hasBeenModified = true
                            timeSlot.title = title
                            timeSlot.description = description
                            // date and time are saved on positive button click
                            timeSlot.duration = duration
                            timeSlot.location = location
                        }
                        
                        // save the list
                        if (!timeSlot.isEmpty() && viewModel.hasBeenModified) {
                            when (viewModel.editFragmentMode) {
                                ADD_MODE -> {
                                    viewModel.addTimeSlot(timeSlot)
                                    activity?.snackBar("Time slot created!")
                                }
                                EDIT_MODE -> {
                                    viewModel.updateTimeSlot(timeSlot)
                                    activity?.snackBar("Time slot updated!")
                                }
                                else -> Unit
                            }
                        }
                        // disable the callback to avoid loops
                        isEnabled = false
                        activity?.onBackPressed()
                    }
                }
            })

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
                if (viewModel.currentTimeslot?.hour != hour
                    || viewModel.currentTimeslot?.minute != minute
                ) {
                    viewModel.hasBeenModified = true
                    viewModel.currentTimeslot?.hour = hour!!
                    viewModel.currentTimeslot?.minute = minute!!
                }
            }
        }

        timePickerButton.hour = viewModel.currentTimeslot?.hour.takeIf { it != 99 }
        timePickerButton.minute = viewModel.currentTimeslot?.minute.takeIf { it != 99 }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.currentTimeslot?.let { timeSlot ->
            val title = binding.titleEt.text.toString()
            val description = binding.descriptionEt.text.toString()
            val duration = binding.durationEt.text.toString()
            val location = binding.locationEt.text.toString()

            if (timeSlot.title != title
                || timeSlot.description != description
                || timeSlot.duration != duration
                || timeSlot.location != location
            ) {
                viewModel.hasBeenModified = true
                timeSlot.title = title
                timeSlot.description = description
                // date and time are saved on positive button click
                timeSlot.duration = duration
                timeSlot.location = location
            }
        }
    }
}