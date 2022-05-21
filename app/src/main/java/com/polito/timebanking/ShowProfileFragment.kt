package com.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

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

        return binding.root
    }
}