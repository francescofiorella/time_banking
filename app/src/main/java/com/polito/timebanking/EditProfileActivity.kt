package com.polito.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class EditProfileActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var photoIV: ImageView
    private lateinit var button: ImageButton
    private lateinit var fNameET: EditText
    private lateinit var nicknameET: EditText
    private lateinit var emailET: EditText
    private lateinit var locationET: EditText
    private lateinit var descriptionET: EditText
    private lateinit var chip1e: Chip
    private lateinit var chip2e: Chip
    private lateinit var chip3e: Chip
    private lateinit var chip4e: Chip
    private lateinit var chip5e: Chip
    private lateinit var chip6e: Chip
    private lateinit var chip7e: Chip
    private lateinit var chip8e: Chip
    private lateinit var chipControl: MutableList<Chip>
    private var skillsArray = arrayListOf<String>()
    private var pickImage = 100

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PHOTO_SAVE_KEY = "photo_save_key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        toolbar = findViewById(R.id.topAppBar)
        photoIV = findViewById(R.id.photo)
        button = findViewById(R.id.edit_photo)
        fNameET = findViewById(R.id.fullName_et)
        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        locationET = findViewById(R.id.location_et)
        descriptionET = findViewById(R.id.description_et)
        chip1e = findViewById(R.id.chip_1e)
        chip2e = findViewById(R.id.chip_2e)
        chip3e = findViewById(R.id.chip_3e)
        chip4e = findViewById(R.id.chip_4e)
        chip5e = findViewById(R.id.chip_5e)
        chip6e = findViewById(R.id.chip_6e)
        chip7e = findViewById(R.id.chip_7e)
        chip8e = findViewById(R.id.chip_8e)
        chipControl = mutableListOf()

        chipControl.add(0, chip1e)
        chipControl.add(1, chip2e)
        chipControl.add(2, chip3e)
        chipControl.add(3, chip4e)
        chipControl.add(4, chip5e)
        chipControl.add(5, chip6e)
        chipControl.add(6, chip7e)
        chipControl.add(7, chip8e)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        button.setOnClickListener {
            showMenu(it, R.menu.insert_photo_menu)
        }



        //photoIV.setImageBitmap(intent.getParcelableExtra(ShowProfileActivity.PHOTO_KEY))
        try {
            val fileInputStream = openFileInput("profileImg.png")
            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            photoIV.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        fNameET.setText(intent.getStringExtra(ShowProfileActivity.FULL_NAME_KEY) ?: "")
        nicknameET.setText(intent.getStringExtra(ShowProfileActivity.NICKNAME_KEY) ?: "")
        emailET.setText(intent.getStringExtra(ShowProfileActivity.EMAIL_KEY) ?: "")
        locationET.setText(intent.getStringExtra(ShowProfileActivity.LOCATION_KEY) ?: "")
        skillsArray =
            intent.getStringArrayListExtra(ShowProfileActivity.SKILLS_KEY) ?: arrayListOf()
        descriptionET.setText(intent.getStringExtra(ShowProfileActivity.DESCRIPTION_KEY) ?: "")

        for (chip in chipControl) {
            chip.setOnClickListener {
                if (chip.isChecked)
                    skillsArray.add(chip.text.toString())
            }
            if (skillsArray.contains(chip.text.toString()))
                chip.isChecked = false
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putParcelable(PHOTO_SAVE_KEY, photoIV.drawable.toBitmap())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
       /* savedInstanceState.getParcelable<Bitmap>(PHOTO_SAVE_KEY)?.let {
            photoIV.setImageBitmap(it)
        } */
        try {
            val fileInputStream = openFileInput("profileImg.png")
            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            photoIV.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_image -> {
                    val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                    startActivityForResult(gallery, pickImage)
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
            description = descriptionET.text.toString()
        )

        for (chip in chipControl) {
            if (!chip.isChecked)
                user.skills.add(chip.text.toString())
        }

        intent.putExtra(ShowProfileActivity.FULL_NAME_KEY, user.fullName)
        intent.putExtra(ShowProfileActivity.NICKNAME_KEY, user.nickName)
        intent.putExtra(ShowProfileActivity.EMAIL_KEY, user.email)
        intent.putExtra(ShowProfileActivity.LOCATION_KEY, user.location)
        intent.putExtra(ShowProfileActivity.DESCRIPTION_KEY, user.description)
        //intent.putExtra(ShowProfileActivity.PHOTO_KEY, photoIV.drawable.toBitmap())
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
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photoIV.setImageBitmap(imageBitmap)
            saveImage(imageBitmap)
        } else if(requestCode == pickImage && resultCode == RESULT_OK){
            val imageUri = data?.data
            val imageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver,imageUri)
            photoIV.setImageBitmap(imageBitmap)
            saveImage(imageBitmap)
            }
    }

    private fun saveImage(photo: Bitmap){
        val filename = "profileImg.png"
        val stream = ByteArrayOutputStream()
        photo.compress(Bitmap.CompressFormat.PNG, 90, stream)
        this.openFileOutput(filename, Context.MODE_PRIVATE).use {
            it.write(stream.toByteArray())
        }
    }

}