package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentTimeSlotDetailBinding
import com.polito.timebanking.viewmodels.TimeSlotViewModel

class TimeSlotDetailsFragment : Fragment(R.layout.fragment_time_slot_detail) {

    private lateinit var binding: FragmentTimeSlotDetailBinding
    private val viewModel by viewModels<TimeSlotViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        println(requireArguments().getString("tslocation"))
        view.findViewById<TextView>(R.id.location_tv).text=requireArguments().getString("tslocation")
        view.findViewById<TextView>(R.id.title_tv).text="prova"



        //binding.descriptionTv.setText(requireArguments().getString("tsdescription"))
        //binding.durationTv.setText(requireArguments().getString("tsduration"))
        //requireArguments().getInt("tshour")
        //requireArguments().getInt("tsyear")
        //requireArguments().getInt("tsday")
        //requireArguments().getInt("tsminute")
        //requireArguments().getInt("tsmonth")



    }
    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_detail, container, false)
        binding.timeSlot = viewModel.timeslot

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)


        return binding.root
    }


}