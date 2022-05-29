package com.polito.timebanking.view.timeslots

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
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentTimeSlotEditBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.utils.DatePickerButton
import com.polito.timebanking.utils.TimePickerButton
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.SkillViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.UserViewModel

class TimeSlotEditFragment : Fragment() {

    private lateinit var binding: FragmentTimeSlotEditBinding

    // custom date and time pickers
    private lateinit var datePickerBtn: DatePickerButton
    private lateinit var timePickerButton: TimePickerButton

    private val timeSlotModel by activityViewModels<TimeSlotViewModel>()
    private val userViewModel by activityViewModels<UserViewModel>()
    private val skillModel by activityViewModels<SkillViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_edit, container, false)

        binding.timeSlot = timeSlotModel.currentTimeSlot

        binding.durationAutoCompleteTV.setOnClickListener(durationListener)
        binding.durationTextInputLayout.setEndIconOnClickListener(durationListener)
        binding.skillAutoCompleteTV.setOnClickListener(userSkillListener)
        binding.skillTextInputLayout.setEndIconOnClickListener(userSkillListener)

        skillModel.skillList.observe(viewLifecycleOwner) { skillList ->
            binding.skillAutoCompleteTV.setText(
                skillList
                    .find { s -> s.sid == binding.timeSlot?.sid }
                    ?.name)
        }

        // save timeSlot onBackPressed
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    timeSlotModel.currentTimeSlot?.let { timeSlot ->
                        saveTimeSlotDataIn(timeSlot)

                        // save the list
                        if (!timeSlot.isEmpty() && timeSlotModel.tsHasBeenModified()) {
                            when (timeSlotModel.editFragmentMode) {
                                ADD_MODE -> {
                                    timeSlotModel.addTimeSlot(timeSlot)
                                    activity?.snackBar("Time slot created!")
                                }
                                EDIT_MODE -> {
                                    timeSlotModel.updateTimeSlot(timeSlot)
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

        // init datePicker
        datePickerBtn = object : DatePickerButton(
            binding.dateTextInputLayout,
            binding.dateAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                timeSlotModel.currentTimeSlot?.year = year!!
                timeSlotModel.currentTimeSlot?.month = month!!
                timeSlotModel.currentTimeSlot?.day = day!!
            }
        }

        datePickerBtn.year = timeSlotModel.currentTimeSlot?.year.takeIf { it != 0 }
        datePickerBtn.month = timeSlotModel.currentTimeSlot?.month.takeIf { it != 0 }
        datePickerBtn.day = timeSlotModel.currentTimeSlot?.day.takeIf { it != 0 }

        // init timePicker
        timePickerButton = object : TimePickerButton(
            binding.timeTextInputLayout,
            binding.timeAutoCompleteTV,
            requireActivity()
        ) {
            override fun onPositiveBtnClickListener() {
                super.onPositiveBtnClickListener()
                if (timeSlotModel.currentTimeSlot?.hour != hour
                    || timeSlotModel.currentTimeSlot?.minute != minute
                ) {
                    timeSlotModel.currentTimeSlot?.hour = hour!!
                    timeSlotModel.currentTimeSlot?.minute = minute!!
                }
            }
        }


        timePickerButton.hour = timeSlotModel.currentTimeSlot?.hour.takeIf { it != 99 }
        timePickerButton.minute = timeSlotModel.currentTimeSlot?.minute.takeIf { it != 99 }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        timeSlotModel.currentTimeSlot?.let { timeSlot ->
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
                timeSlotModel.currentTimeSlot?.apply {
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
        timeSlotModel.currentTimeSlot?.apply {
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