package com.polito.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.exifinterface.media.ExifInterface
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.IOException


class EditProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var photoIV: ImageView
    private lateinit var editPhotoIB: ImageButton
    private lateinit var fNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText
    private lateinit var skillsCG: ChipGroup
    private lateinit var descriptionET: EditText
    private var skillsList = arrayListOf<String>()

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_PICK_IMAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        toolbar = findViewById(R.id.topAppBar)
        photoIV = findViewById(R.id.photo_iv)
        editPhotoIB = findViewById(R.id.edit_photo_ib)
        fNameET = findViewById(R.id.full_name_et)
        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        locationET = findViewById(R.id.location_et)
        skillsCG = findViewById(R.id.skills_cg)
        descriptionET = findViewById(R.id.description_et)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        editPhotoIB.setOnClickListener {
            showMenu(it, R.menu.edit_photo_menu)
        }

        setImageFromStorage()

        fNameET.setText(intent.getStringExtra(ShowProfileActivity.FULL_NAME_KEY) ?: "")
        nicknameET.setText(intent.getStringExtra(ShowProfileActivity.NICKNAME_KEY) ?: "")
        emailET.setText(intent.getStringExtra(ShowProfileActivity.EMAIL_KEY) ?: "")
        locationET.setText(intent.getStringExtra(ShowProfileActivity.LOCATION_KEY) ?: "")
        skillsList = intent.getStringArrayListExtra(ShowProfileActivity.SKILLS_KEY) ?: arrayListOf()
        descriptionET.setText(intent.getStringExtra(ShowProfileActivity.DESCRIPTION_KEY) ?: "")

        for (idx in 0 until skillsCG.childCount) {
            val skillC: Chip = skillsCG.getChildAt(idx) as Chip
            skillC.isChecked = skillsList.contains(skillC.text.toString())
            skillC.setOnCheckedChangeListener { chip, isChecked ->
                if (isChecked) {
                    skillsList.add(chip.text.toString())
                } else {
                    skillsList.remove(chip.text.toString())
                }
            }
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        setImageFromStorage()
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_image -> {
                    val gallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, REQUEST_PICK_IMAGE)
                    true
                }
                R.id.take_image -> {
                    dispatchTakePictureIntent()
                    true
                }
                else -> false
            }
        }

        // Show the popup menu.
        popup.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent()

        val user = User(
            fNameET.text.toString(),
            nicknameET.text.toString(),
            emailET.text.toString(),
            locationET.text.toString(),
            skillsList,
            descriptionET.text.toString()
        )

        intent.putExtra(ShowProfileActivity.FULL_NAME_KEY, user.fullName)
        intent.putExtra(ShowProfileActivity.NICKNAME_KEY, user.nickName)
        intent.putExtra(ShowProfileActivity.EMAIL_KEY, user.email)
        intent.putExtra(ShowProfileActivity.LOCATION_KEY, user.location)
        intent.putExtra(ShowProfileActivity.DESCRIPTION_KEY, user.description)
        intent.putExtra(ShowProfileActivity.SKILLS_KEY, user.skills)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // display error state to the user
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            saveImageToStorage(imageBitmap)
            val rotatedImageBitmap =
                rotateImageIfRequired(imageBitmap, ShowProfileActivity.PATH_PHOTO)
            photoIV.setImageBitmap(rotatedImageBitmap)
        } else if (requestCode == REQUEST_PICK_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { imageUri ->
                var imageBitmap: Bitmap? = null
                try {
                    imageBitmap = if (Build.VERSION.SDK_INT < 28) {
                        @Suppress("DEPRECATION")
                        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    } else {
                        val source: ImageDecoder.Source =
                            ImageDecoder.createSource(contentResolver, imageUri)
                        ImageDecoder.decodeBitmap(source)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                imageBitmap?.let {
                    photoIV.setImageBitmap(it)
                    saveImageToStorage(it)
                }
            }
        }
    }

    private fun setImageFromStorage() {
        try {
            val fileInputStream = openFileInput(ShowProfileActivity.PATH_PHOTO)
            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            photoIV.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun saveImageToStorage(photo: Bitmap) {
        val stream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
        this.openFileOutput(ShowProfileActivity.PATH_PHOTO, Context.MODE_PRIVATE).use {
            it.write(stream.toByteArray())
        }
    }

    private fun rotateImageIfRequired(bitmap: Bitmap, pathname: String): Bitmap {
        try {
            val fileInputStream = openFileInput(pathname)
            val ei = ExifInterface(fileInputStream)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            return when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270f)
                ExifInterface.ORIENTATION_NORMAL -> bitmap
                else -> bitmap
            }
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException, is IOException -> {
                    e.printStackTrace()
                    return bitmap
                }
                else -> throw e
            }
        }
    }

    private fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }
}