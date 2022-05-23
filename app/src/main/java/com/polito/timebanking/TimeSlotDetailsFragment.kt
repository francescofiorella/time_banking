package com.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)

        binding.timeSlot = timeSlotModel.currentTimeSlot

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
            userModel.getUser(uid)
            findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_showUserFragment)
        }
    }
}