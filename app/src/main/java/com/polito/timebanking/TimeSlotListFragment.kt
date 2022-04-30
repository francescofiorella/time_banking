package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.polito.timebanking.databinding.FragmentTimeSlotListBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.utils.TimeSlotAdapter
import com.polito.timebanking.utils.TimeSlotListener

class TimeSlotListFragment: Fragment(), TimeSlotListener{

    private lateinit var binding: FragmentTimeSlotListBinding

    // TODO: implements a local database with room
    private val timeSlotList = mutableListOf(
        TimeSlot(title = "Time Slot 1", location = "Location 1", year = 2020, month = 10, day = 12),
        TimeSlot(title = "Time Slot 2", location = "Location 2", year = 2020, month = 9, day = 14),
        TimeSlot(title = "Time Slot 3", location = "Location 3", year = 2020, month = 8, day = 30),
        TimeSlot(title = "Time Slot 4", location = "Location 4", year = 2020, month = 10, day = 4)
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_list, container, false)

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        TimeSlotAdapter(timeSlotList,this).also {
            binding.listRecyclerView.adapter = it
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }
    }

    override fun onCardClickListener(timeSlot: TimeSlot, position: Int) {
        // TODO: pass timeSlot parameters to detailFragment
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment, Bundle().apply{
            putString("timeslot_key", Gson().toJson(timeSlotList[position]) ?: "")
        })
    }

    override fun onEditClickListener(timeSlot: TimeSlot, position: Int) {
        // TODO: pass timeSlot parameters to editFragment
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment, Bundle().apply{

            putString("tstitle", timeSlotList.elementAt(position).title)
            //putString("tsdescription",timeSlotList.elementAt(position).description)
            //putString("tsduration",timeSlotList.elementAt(position).duration)
            putString("tslocation",timeSlotList.elementAt(position).location)
        })
    }
}