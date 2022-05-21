package com.polito.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.polito.timebanking.databinding.FragmentEditProfileBinding
import com.polito.timebanking.utils.rotateBitmap
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.viewmodels.UserViewModel

class EditProfileFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var binding: FragmentEditProfileBinding

    private val selectPhotoForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                activityResult.data?.data?.let { imageUri ->
                    var bitmap: Bitmap? = null
                    try {
                        bitmap = if (Build.VERSION.SDK_INT < 28) {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                imageUri
                            )
                        } else {
                            val source: ImageDecoder.Source =
                                ImageDecoder.createSource(
                                    requireContext().contentResolver,
                                    imageUri
                                )
                            ImageDecoder.decodeBitmap(source)
                        }
                    } catch (e: java.lang.Exception) {
                        Log.e("EditProfileFragment", "selectPhotoForActivityResult", e)
                    }
                    bitmap?.let {
                        val rotatedBitmap = rotateBitmap(requireContext(), it)
                        userModel.setPhoto(rotatedBitmap)
                    }
                }
            }
        }

    private val takePhotoForActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val bitmap = activityResult.data?.extras?.get("data") as Bitmap
                val rotatedBitmap = rotateBitmap(requireContext(), bitmap)
                userModel.setPhoto(rotatedBitmap)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_edit_profile, container, false)

        binding.viewmodel = userModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)

        binding.editPhotoIb.setOnClickListener {
            showMenu(it, R.menu.edit_photo_menu)
        }

        userModel.photoBitmap.observe(viewLifecycleOwner) { photoBitmap ->
            Log.d(
                "EditProfileFragment",
                "userModel.photoBitmap.observe (photoBitmap = ${photoBitmap})"
            )
            if (photoBitmap != null) {
                binding.photoIv.setImageBitmap(photoBitmap)
            }
        }

        userModel.skills.observe(viewLifecycleOwner) { skills ->
            Log.d(
                "EditProfileFragment",
                "userModel.skills.observe (skills = ${skills})"
            )
            binding.skillsCg.removeAllViews()
            skills.forEach { skill ->
                val chip = layoutInflater.inflate(
                    R.layout.layout_skill_chip,
                    binding.skillsCg,
                    false
                ) as Chip
                chip.isChecked =
                    userModel.currentUser.value?.skills?.any { s -> s.sid == skill.sid } == true
                chip.isCheckedIconVisible = true
                chip.text = skill.name
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        userModel.currentUser.value?.skills?.add(skill)
                    } else {
                        userModel.currentUser.value?.skills?.removeIf { s -> s.sid == skill.sid }
                    }
                }
                binding.skillsCg.addView(chip)
            }
        }

        return binding.root
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_image -> {
                    dispatchSelectPhotoIntent()
                    true
                }
                R.id.take_image -> {
                    dispatchTakePhotoIntent()
                    true
                }
                else -> false
            }
        }

        popup.show()
    }

    private fun dispatchSelectPhotoIntent() {
        val selectPhotoIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        try {
            selectPhotoForActivityResult.launch(selectPhotoIntent)
        } catch (e: ActivityNotFoundException) {
            Log.e("EditProfileFragment", "dispatchSelectPhotoIntent", e)
        }
    }

    private fun dispatchTakePhotoIntent() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            takePhotoForActivityResult.launch(takePhotoIntent)
        } catch (e: ActivityNotFoundException) {
            Log.e("EditProfileFragment", "dispatchTakePhotoIntent", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userModel.currentUser.value?.let { userModel.setUser(it, false) }
        activity?.snackBar("User updated!")
    }
}