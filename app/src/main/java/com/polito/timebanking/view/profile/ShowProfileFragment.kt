package com.polito.timebanking.view.profile

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentShowProfileBinding
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.USER_ID_KEY
import com.polito.timebanking.viewmodels.UserViewModel

class ShowProfileFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var binding: FragmentShowProfileBinding

    companion object {
        const val JUST_SHOW = 100
        const val SHOW_AND_EDIT = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_show_profile, container, false)

        binding.viewmodel = userModel
        userModel.isLoading.value = true

        val uid = arguments?.getString(USER_ID_KEY)
        if (uid != null) {
            userModel.profileMode = JUST_SHOW
            userModel.getCurrentUser(uid)
        } else {
            userModel.profileMode = SHOW_AND_EDIT
            userModel.getCurrentUser()
        }

        binding.lifecycleOwner = this.viewLifecycleOwner

        userModel.currentUser.observe(viewLifecycleOwner) { currentUser ->
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUser.observe (currentUser = ${currentUser})"
            )

            binding.skillsCg.removeAllViews()
            currentUser?.skills?.forEach { skill ->
                val chip = layoutInflater.inflate(
                    R.layout.layout_skill_chip,
                    binding.skillsCg,
                    false
                ) as Chip
                chip.isCheckable = false
                chip.text = skill.name
                binding.skillsCg.addView(chip)
            }
        }

        userModel.currentUserBitmap.observe(viewLifecycleOwner) { currentUserBitmap ->
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUserBitmap.observe (currentUserBitmap = ${currentUserBitmap})"
            )
            if (currentUserBitmap != null) {
                binding.ivPhoto.setImageBitmap(currentUserBitmap)
            }
        }

        userModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.profileLayout.isVisible = !isLoading
            binding.loadingCpi.isVisible = isLoading
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            if (userModel.profileMode == SHOW_AND_EDIT) {
                getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (userModel.profileMode == SHOW_AND_EDIT) {
            (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
        } else {
            (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (userModel.profileMode == SHOW_AND_EDIT) {
            inflater.inflate(R.menu.toolbar_menu, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                userModel.setInitialUser()
                findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                true
            }

            android.R.id.home -> {
                if (userModel.profileMode == SHOW_AND_EDIT) {
                    (activity as MainActivity).getDrawerLayout().open()
                } else {
                    activity?.onBackPressed()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}