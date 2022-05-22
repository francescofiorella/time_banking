package com.polito.timebanking

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.NONE

class TimeSlotDetailsFragment : Fragment() {

    private lateinit var binding: FragmentTimeSlotDetailBinding
    private val viewModel by activityViewModels<TimeSlotViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)

        binding.timeSlot = viewModel.currentTimeSlot

        setHasOptionsMenu(viewModel.isCurrentTimeSlotMine())

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        viewModel.editFragmentMode = NONE
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                viewModel.setTimeSlot(viewModel.currentTimeSlot)
                viewModel.editFragmentMode = EDIT_MODE
                findNavController().navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}