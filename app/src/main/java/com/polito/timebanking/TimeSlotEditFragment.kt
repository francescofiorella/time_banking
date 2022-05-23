package com.polito.timebanking

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.UserViewModel

class TimeSlotEditFragment : Fragment() {

    private lateinit var binding: FragmentTimeSlotEditBinding

    // custom date and time pickers
    private lateinit var datePickerBtn: DatePickerButton
    private lateinit var timePickerButton: TimePickerButton

    private val viewModel by activityViewModels<TimeSlotViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_edit, container, false)

        binding.timeSlot = viewModel.currentTimeSlot

        binding.durationAutoCompleteTV.setOnClickListener(durationListener)
        binding.durationTextInputLayout.setEndIconOnClickListener(durationListener)
        binding.skillAutoCompleteTV.setOnClickListener(userSkillListener)

        // save timeSlot onBackPressed
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.currentTimeSlot?.let { timeSlot ->
                        saveTimeSlotDataIn(timeSlot)

                        // save the list
                        if (!timeSlot.isEmpty() && viewModel.tsHasBeenModified()) {
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

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // init skillPicker

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val userSkillListener = View.OnClickListener {
        val skillArray =
            userViewModel.currentUser.value?.skills?.map { skill -> skill.name }?.toTypedArray()
        val builder = MaterialAlertDialogBuilder(requireContext())
        skillArray?.also {
            builder.setSingleChoiceItems(
                skillArray,
                skillArray.indexOf(binding.skillAutoCompleteTV.text.toString()),
                userSkillOnSaveListener
            )
        }
        builder.show()
    }

    private val userSkillOnSaveListener = DialogInterface.OnClickListener { dialog, selectedItem ->
        val skillsName =
            userViewModel.currentUser.value?.skills?.map { skill -> skill.name }?.toTypedArray()
        val skillsSid =
            userViewModel.currentUser.value?.skills?.map { skill -> skill.sid }?.toTypedArray()
        skillsSid?.also {
            val skillSid = skillsSid[selectedItem]
            skillSid?.also {
                viewModel.currentTimeSlot?.apply {
                    sid = skillSid
                }
            }
            skillsName?.also {
                binding.skillAutoCompleteTV.setText(skillsName[selectedItem])
            }
        }
        dialog.dismiss()
    }

    private val durationListener = View.OnClickListener {
        val durationArray = resources.getStringArray(R.array.durations)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setIcon(R.drawable.ic_av_timer)
        builder.setTitle(getString(R.string.duration))
        builder.setSingleChoiceItems(
            durationArray,
            durationArray.indexOf(binding.durationAutoCompleteTV.text.toString()),
            durationOnSaveListener
        )
        builder.show()
    }

    private val durationOnSaveListener = DialogInterface.OnClickListener { dialog, selectedItem ->
        val durationArray = resources.getStringArray(R.array.durations)
        val durationString = durationArray[selectedItem]
        viewModel.currentTimeSlot?.apply {
            duration = durationString
        }
        binding.durationAutoCompleteTV.setText(durationString)
        dialog.dismiss()
    }

    private fun saveTimeSlotDataIn(timeSlot: TimeSlot) {
        timeSlot.title = binding.titleEt.text.toString()
        timeSlot.description = binding.descriptionEt.text.toString()
        timeSlot.location = binding.locationEt.text.toString()
        // date, time and duration are saved on positive button click
    }
}