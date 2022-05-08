package com.polito.timebanking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.polito.timebanking.utils.rotateBitmap
import com.polito.timebanking.viewmodels.UserViewModel
import java.io.FileNotFoundException

class ShowProfileFragment : Fragment() {

    private lateinit var binding: FragmentShowProfileBinding
    private val userModel by activityViewModels<UserViewModel>()

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

        userModel.currentUser.observe(viewLifecycleOwner) {
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUser.observe - $it"
            )
            if (userModel.currentUserBitmap.value == null) {
                it.photoPath?.let { photoPath -> setPhotoFromStorage(photoPath) }
            }
        }

        userModel.currentUserSkills.observe(viewLifecycleOwner) {
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUserSkills.observe - $it"
            )
            userModel.currentUserCheckedSkills.value = it.toMutableList()
        }

        userModel.currentUserBitmap.observe(viewLifecycleOwner) {
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUserBitmap.observe - $it"
            )
            binding.ivPhoto.setImageBitmap(it)
        }

        userModel.currentUserCheckedSkills.observe(viewLifecycleOwner) {
            Log.d(
                "ShowProfileFragment",
                "userModel.currentUserCheckedSkills.observe - $it"
            )
            binding.skillsCg.removeAllViews()
            it.forEach { skill ->
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

        return binding.root
    }

    private fun setPhotoFromStorage(path: String) {
        try {
            val fileInputStream = requireContext().openFileInput(path)
            val bitmap: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            val rotatedBitmap = rotateBitmap(requireContext(), bitmap, path)
            userModel.currentUserBitmap.value = rotatedBitmap
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }
}