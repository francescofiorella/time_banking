package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_list, container, false)

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        TimeSlotAdapter(viewModel.timeSlotList, this).also {
            binding.listRecyclerView.adapter = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            viewModel.editFragmentMode = ADD_MODE
            viewModel.currentTimeslot = TimeSlot()
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
            /*findNavController().navigate(
                R.id.action_timeSlotListFragment_to_timeSlotEditFragment,
                Bundle().apply {
                    putString(TIMESLOT_KEY, null)
                })*/
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.editFragmentMode = NONE
        viewModel.currentTimeslot = null
    }

    override fun onCardClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = NONE
        viewModel.currentTimeslot = timeSlot
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
        /*findNavController().navigate(
            R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment,
            Bundle().apply {
                putString(TIMESLOT_KEY, Gson().toJson(viewModel.timeSlotList[position]) ?: "")
            })*/
    }

    override fun onEditClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = EDIT_MODE
        viewModel.currentTimeslot = timeSlot
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        /*findNavController().navigate(
            R.id.action_timeSlotListFragment_to_timeSlotEditFragment,
            Bundle().apply {
                putString(TIMESLOT_KEY, Gson().toJson(viewModel.timeSlotList[position]) ?: "")
            })*/
    }
}