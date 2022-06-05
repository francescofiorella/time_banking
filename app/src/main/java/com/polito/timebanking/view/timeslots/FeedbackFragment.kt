package com.polito.timebanking.view.timeslots

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentFeedbackBinding
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.FeedbackViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.UserViewModel

class FeedbackFragment: Fragment() {
    private lateinit var binding : FragmentFeedbackBinding

    private val timeSlotModel by activityViewModels<TimeSlotViewModel>()
    private val userModel by activityViewModels<UserViewModel>()
    private val feedbackModel by activityViewModels<FeedbackViewModel>()

    val feedback = Feedback();


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_feedback, container, false)

        binding.sendFeedbackBtn.setOnClickListener(feedbackListener)
        feedbackModel.setFeedback(feedback)

        //setting feedback time slot id
        feedbackModel.currentFeedback!!.tsid = timeSlotModel.currentTimeSlot!!.id

        //send timeslot and user full info
        binding.timeslot = timeSlotModel.currentTimeSlot

        userModel.isLoading.value = true

        val dest = arguments?.getString(TimeSlotListFragment.SOURCE)
        if(dest=="MINE"){
            // if coming from My TimeSlot option, set uid as cid
            userModel.getUser(timeSlotModel.currentTimeSlot!!.cid)
            feedbackModel.currentFeedback!!.destuid = timeSlotModel.currentTimeSlot!!.cid
        }else{
            // if coming from Required, then remains uid
            userModel.getUser(timeSlotModel.currentTimeSlot!!.uid)
            feedbackModel.currentFeedback!!.destuid = timeSlotModel.currentTimeSlot!!.uid

        }



        binding.lifecycleOwner = this.viewLifecycleOwner

        val showedUser = userModel.user
        binding.user = showedUser

        showedUser.observe(viewLifecycleOwner){ user ->
            if (user?.photoUrl.isNullOrEmpty()) {
                binding.ivPhoto.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.ivPhoto.context,
                        R.drawable.ic_user
                    )
                )
            } else {
                Glide.with(binding.ivPhoto)
                    .load(user?.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.ivPhoto)
            }
            userModel.isLoading.value = false
        }

        userModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.profileLayout.isVisible = !isLoading
            binding.loadingCpi.isVisible = isLoading
        }


        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.title)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timeslot?.let { timeSlot ->
            userModel.getUser(timeSlot.uid)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }


    private fun saveFeedbackDataIn(feedback: Feedback?) {
        feedback!!.comment = binding.commentEt.text.toString()
        feedback!!.rate = binding.ratingbar.rating
    }

    private val feedbackListener = View.OnClickListener {
        saveFeedbackDataIn(feedbackModel.currentFeedback)
        feedbackModel.addFeedback(feedbackModel.currentFeedback!!)
        findNavController().navigate(R.id.timeSlotListFragment)
    }



}