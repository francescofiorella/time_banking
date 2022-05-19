package com.polito.timebanking

import android.app.Activity.RESULT_OK
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
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.polito.timebanking.databinding.FragmentEditProfileBinding
import com.polito.timebanking.utils.rotateBitmap
import com.polito.timebanking.utils.saveBitmapToStorage
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.viewmodels.UserViewModel

class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding
    private val userModel by activityViewModels<UserViewModel>()

    companion object {
        private const val REQUEST_CODE_SELECT_PHOTO = 100
        private const val REQUEST_CODE_TAKE_PHOTO = 200
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
            startActivityForResult(selectPhotoIntent, REQUEST_CODE_SELECT_PHOTO)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun dispatchTakePhotoIntent() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePhotoIntent, REQUEST_CODE_TAKE_PHOTO)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val path = "profilePhoto_${userModel.currentUser.value?.uid}.png"
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT_PHOTO -> {
                    data?.data?.let { imageUri ->
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
                            e.printStackTrace()
                        }
                        bitmap?.let { it ->
                            saveBitmapToStorage(requireContext(), it, path)
                            userModel.currentUser.value?.let { currentUser ->
                                currentUser.photoPath = path
                            }
                            val rotatedBitmap = rotateBitmap(requireContext(), it, path)
                            userModel.updatePhotoBitmap(rotatedBitmap)
                        }
                    }
                }
                REQUEST_CODE_TAKE_PHOTO -> {
                    val bitmap = data?.extras?.get("data") as Bitmap
                    saveBitmapToStorage(requireContext(), bitmap, path)
                    userModel.currentUser.value?.let { it.photoPath = path }
                    val rotatedBitmap = rotateBitmap(requireContext(), bitmap, path)
                    userModel.updatePhotoBitmap(rotatedBitmap)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        userModel.currentUser.value?.let { userModel.updateUser(it) }
        activity?.snackBar("User updated!")
    }
}