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
import com.polito.timebanking.models.TimeSlot
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

        binding.timeSlot = viewModel.currentTimeSlot

        // save timeSlot onBackPressed
        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.currentTimeSlot?.let { timeSlot ->
                        saveTimeSlotDataIn(timeSlot)

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
                viewModel.currentTimeSlot?.year = year!!
                viewModel.currentTimeSlot?.month = month!!
                viewModel.currentTimeSlot?.day = day!!
            }
        }

        datePickerBtn.year = viewModel.currentTimeSlot?.year.takeIf { it != 0 }
        datePickerBtn.month = viewModel.currentTimeSlot?.month.takeIf { it != 0 }
        datePickerBtn.day = viewModel.currentTimeSlot?.day.takeIf { it != 0 }

        // init timePicker
        timePickerButton = object : TimePickerButton(
            binding.timeTextInputLayout,
            binding.timeAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                if (viewModel.currentTimeSlot?.hour != hour
                    || viewModel.currentTimeSlot?.minute != minute
                ) {
                    viewModel.hasBeenModified = true
                    viewModel.currentTimeSlot?.hour = hour!!
                    viewModel.currentTimeSlot?.minute = minute!!
                }
            }
        }

        timePickerButton.hour = viewModel.currentTimeSlot?.hour.takeIf { it != 99 }
        timePickerButton.minute = viewModel.currentTimeSlot?.minute.takeIf { it != 99 }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.currentTimeSlot?.let { timeSlot ->
            saveTimeSlotDataIn(timeSlot)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    private fun saveTimeSlotDataIn(timeSlot: TimeSlot) {
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