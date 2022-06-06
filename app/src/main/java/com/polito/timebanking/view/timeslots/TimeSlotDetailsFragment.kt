package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.SkillViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.NONE
import com.polito.timebanking.viewmodels.UserViewModel

class TimeSlotDetailsFragment : Fragment() {
    private val timeSlotModel by activityViewModels<TimeSlotViewModel>()
    private val userModel by activityViewModels<UserViewModel>()
    private val skillModel by activityViewModels<SkillViewModel>()

    private lateinit var binding: FragmentTimeSlotDetailBinding

    companion object {
        const val USER_ID_KEY = "user_id_key"
        const val TIMESLOT_ID_KEY = "timeslot_id_key"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)

        binding.timeSlot = timeSlotModel.currentTimeSlot

        binding.statusTv.text = when (timeSlotModel.currentTimeSlot?.state) {
            "completed" -> "Status: Completed"
            "accepted" -> "Status: Accepted"
            else -> "Status: Available"
        }

        binding.userCard.setOnClickListener(userListener)

        userModel.user.observe(viewLifecycleOwner) { user ->
            binding.userTv.text = user?.fullName
        }

        skillModel.skillList.observe(viewLifecycleOwner) { skillList ->
            binding.skillTv.text =
                skillList
                    .find { s -> s.sid == timeSlotModel.currentTimeSlot?.sid }
                    ?.name
        }

        if (timeSlotModel.isCurrentTimeSlotMine() || !timeSlotModel.isTimeSlotAvailable()) {
            binding.openChatFab.isVisible = false
        } else {
            binding.openChatFab.setOnClickListener {
                timeSlotModel.currentTimeSlot?.id?.let {
                    val bundle = bundleOf(TIMESLOT_ID_KEY to it)
                    findNavController().navigate(
                        R.id.action_timeSlotDetailsFragment_to_chatFragment,
                        bundle
                    )
                }
            }
            binding.openChatFab.isVisible = true
        }

        timeSlotModel.currentTimeSlot?.let { timeSlot ->
            val timeCredit =
                "${timeSlot.timeCredit} ${if (timeSlot.timeCredit == 1) "hour" else "hours"}"
            binding.timeCreditTv.text = timeCredit
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.time_slot)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.timeSlot?.let { timeSlot ->
            userModel.getUser(timeSlot.uid)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        timeSlotModel.editFragmentMode = NONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (timeSlotModel.isCurrentTimeSlotMine()) {
            inflater.inflate(R.menu.toolbar_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                timeSlotModel.setTimeSlot(timeSlotModel.currentTimeSlot)
                timeSlotModel.editFragmentMode = EDIT_MODE
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment)
                true
            }

            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val userListener = View.OnClickListener {
        binding.timeSlot?.uid?.let { uid ->
            val bundle = bundleOf(USER_ID_KEY to uid)
            findNavController().navigate(
                R.id.action_timeSlotDetailsFragment_to_showProfileFragment,
                bundle
            )
        }
    }
}