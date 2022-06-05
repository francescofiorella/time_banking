package com.polito.timebanking.view.profile

import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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

        userModel.isLoading.value = true

        val uid = arguments?.getString(USER_ID_KEY)
        arguments?.clear()
        if (uid != null) {
            userModel.profileMode = JUST_SHOW
            userModel.getUser(uid)
        } else {
            userModel.profileMode = SHOW_AND_EDIT
            userModel.getCurrentUser()
        }

        binding.lifecycleOwner = this.viewLifecycleOwner

        val showedUser = if (userModel.profileMode == JUST_SHOW) {
            userModel.user
        } else {
            userModel.currentUser
        }
        binding.user = showedUser
        showedUser.observe(viewLifecycleOwner) { user ->
            binding.skillsCg.removeAllViews()
            user?.skills?.forEach { skill ->
                val chip = layoutInflater.inflate(
                    R.layout.layout_skill_chip,
                    binding.skillsCg,
                    false
                ) as Chip
                chip.isCheckable = false
                chip.text = skill.name
                binding.skillsCg.addView(chip)
            }

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

        if (userModel.profileMode == JUST_SHOW) {
            // Others profile (user)
            userModel.getRatings(uid!!,"writeruid")
            userModel.getRatings(uid!!,"destuid")

            binding.userRatingbar.rating = userModel.user.value!!.userRating!!
            binding.givenRatingbar.rating = userModel.user.value!!.givenRatings!!

        } else {
            // My profile (currentUser)
            userModel.getRatings(userModel.currentUser.value!!.uid!!,"writeruid")
            userModel.getRatings(userModel.currentUser.value!!.uid!!,"destuid")

            binding.userRatingbar.rating = userModel.currentUser.value!!.userRating!!
            binding.givenRatingbar.rating = userModel.currentUser.value!!.givenRatings!!
        }





        userModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.profileLayout.isVisible = !isLoading
            binding.loadingCpi.isVisible = isLoading
        }

        (activity as MainActivity).apply(fun MainActivity.() {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.profile)
            if (userModel.profileMode == SHOW_AND_EDIT) {
                getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        })
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