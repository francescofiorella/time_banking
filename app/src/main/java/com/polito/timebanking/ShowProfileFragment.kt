package com.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.polito.timebanking.databinding.FragmentShowProfileBinding
import com.polito.timebanking.viewmodels.UserViewModel

class ShowProfileFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var binding: FragmentShowProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_show_profile, container, false)

        binding.viewmodel = userModel

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

        userModel.photoBitmap.observe(viewLifecycleOwner) { photoBitmap ->
            Log.d(
                "ShowProfileFragment",
                "userModel.photoBitmap.observe (photoBitmap = ${photoBitmap})"
            )
            if (photoBitmap != null) {
                binding.ivPhoto.setImageBitmap(photoBitmap)
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit -> {
                userModel.setInitialUser()
                findNavController().navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                true
            }

            android.R.id.home -> {
                (activity as MainActivity).getDrawerLayout().open()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}