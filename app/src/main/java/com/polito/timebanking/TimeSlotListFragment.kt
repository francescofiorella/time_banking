package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotListBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.utils.TimeSlotAdapter
import com.polito.timebanking.utils.TimeSlotListener
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.NONE

class TimeSlotListFragment : Fragment(), TimeSlotListener {
    private lateinit var binding: FragmentTimeSlotListBinding
    private val viewModel by activityViewModels<TimeSlotViewModel>()

    companion object {
        const val SKILL_KEY = "skill_key"
        const val MODE_KEY = "mode_key"
        const val MY_LIST = 1
        const val SKILL_LIST = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_list, container, false)

        if (savedInstanceState == null) {
            viewModel.listFragmentMode = arguments?.getInt(MODE_KEY) ?: MY_LIST
            viewModel.sid = arguments?.getString(SKILL_KEY) ?: ""
        }

        viewModel.timeSlotList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.noTimeSlotsTv.isVisible = true
                binding.listRecyclerView.isVisible = false
            } else {
                binding.noTimeSlotsTv.isVisible = false
                binding.listRecyclerView.isVisible = true
                TimeSlotAdapter(list, this).also {
                    binding.listRecyclerView.adapter = it
                }
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            viewModel.editFragmentMode = ADD_MODE
            viewModel.currentTimeSlot = TimeSlot()
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(
            if (isShowingMyTimeSlots()) {
                R.drawable.ic_menu
            } else {
                R.drawable.ic_arrow_back
            }
        )
        viewModel.editFragmentMode = NONE
        viewModel.currentTimeSlot = null
        viewModel.loadList()
    }

    override fun onCardClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = NONE
        viewModel.currentTimeSlot = timeSlot
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
    }

    override fun onEditClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = EDIT_MODE
        viewModel.currentTimeSlot = timeSlot
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
    }

    fun isShowingMyTimeSlots(): Boolean {
        return viewModel.listFragmentMode == MY_LIST
    }
}