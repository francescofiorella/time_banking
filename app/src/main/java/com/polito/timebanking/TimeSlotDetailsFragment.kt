package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.viewmodels.TimeSlotViewModel
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

    override fun onResume() {
        super.onResume()
        viewModel.editFragmentMode = NONE
    }
}