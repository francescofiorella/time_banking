package com.polito.timebanking

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip
import com.google.gson.Gson
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException

class ShowProfileActivity : AppCompatActivity() {

    companion object {
        const val EDIT_KEY = 10
        const val PHOTO_KEY = "group36.lab1.PHOTO"
        const val FULL_NAME_KEY = "group36.lab1.FULL_NAME"
        const val NICKNAME_KEY = "group36.lab1.NICKNAME"
        const val EMAIL_KEY = "group36.lab1.EMAIL"
        const val LOCATION_KEY = "group36.lab1.LOCATION"
        const val SKILLS_KEY = "group36.lab1.SKILLS"
        const val DESCRIPTION_KEY = "group36.lab1.DESCRIPTION"

        private const val PHOTO_SAVE_KEY = "photo_save_key"
        private const val FULL_NAME_SAVE_KEY = "fullName_save_key"
        private const val NICKNAME_SAVE_KEY = "nickname_save_key"
        private const val EMAIL_SAVE_KEY = "email_save_key"
        private const val LOCATION_SAVE_KEY = "location_save_key"
        private const val SKILLS_SAVE_KEY = "skills_save_key"
        private const val DESCRIPTION_SAVE_KEY = "description_save_key"

        private const val SHARED_KEY = "shared_key"
        private const val PROFILE_KEY = "profile"
    }

    private lateinit var toolbar: MaterialToolbar
    private lateinit var photoIV: ImageView
    private lateinit var fNameTV: TextView
    private lateinit var nicknameTV: TextView
    private lateinit var emailTV: TextView
    private lateinit var locationTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var chip1: Chip
    private lateinit var chip2: Chip
    private lateinit var chip3: Chip
    private lateinit var chip4: Chip
    private lateinit var chip5: Chip
    private lateinit var chip6: Chip
    private lateinit var chip7: Chip
    private lateinit var chip8: Chip
    private lateinit var chipControl: MutableList<Chip>
    private lateinit var photo: Bitmap
    private var skillsArray = arrayListOf<String>()
    private var sharedPref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        toolbar = findViewById(R.id.topAppBar)
        photoIV = findViewById(R.id.iv_photo)
        fNameTV = findViewById(R.id.tv_fullname)
        nicknameTV = findViewById(R.id.tv_nickname)
        emailTV = findViewById(R.id.tv_email)
        locationTV = findViewById(R.id.tv_location)
        descriptionTV = findViewById(R.id.tv_description)
        chip1 = findViewById(R.id.chip_1)
        chip2 = findViewById(R.id.chip_2)
        chip3 = findViewById(R.id.chip_3)
        chip4 = findViewById(R.id.chip_4)
        chip5 = findViewById(R.id.chip_5)
        chip6 = findViewById(R.id.chip_6)
        chip7 = findViewById(R.id.chip_7)
        chip8 = findViewById(R.id.chip_8)
        chipControl = mutableListOf()

        chipControl.add(0, chip1)
        chipControl.add(1, chip2)
        chipControl.add(2, chip3)
        chipControl.add(3, chip4)
        chipControl.add(4, chip5)
        chipControl.add(5, chip6)
        chipControl.add(6, chip7)
        chipControl.add(7, chip8)

        sharedPref = applicationContext?.getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE)

        if (savedInstanceState == null) {
            sharedPref?.getString(PROFILE_KEY, "")?.let {
                if (it != "") {
                    val user = Gson().fromJson(it, User::class.java)
                    fNameTV.text = user.fullName
                    nicknameTV.text = user.nickName
                    emailTV.text = user.email
                    locationTV.text = user.location
                    skillsArray = user.skills
                    descriptionTV.text = user.description
                }
            }
        }

        try {
            val fileInputStream = openFileInput("profileImg.png")
            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            photoIV.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        for (skill in skillsArray) {
            for (chip in chipControl) {
                if (chip.text == skill)
                    chip.visibility = View.VISIBLE
            }
        }

        toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    editProfile()
                    true
                }

                else -> false
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putParcelable(PHOTO_SAVE_KEY, photoIV.drawable.toBitmap())
        outState.putString(FULL_NAME_SAVE_KEY, fNameTV.text.toString())
        outState.putString(NICKNAME_SAVE_KEY, nicknameTV.text.toString())
        outState.putString(EMAIL_SAVE_KEY, emailTV.text.toString())
        outState.putString(LOCATION_SAVE_KEY, locationTV.text.toString())
        outState.putStringArrayList(SKILLS_SAVE_KEY, skillsArray)
        outState.putString(DESCRIPTION_SAVE_KEY, descriptionTV.text.toString())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        /*
        savedInstanceState.getParcelable<Bitmap>(PHOTO_SAVE_KEY)?.let {
            photoIV.setImageBitmap(it)
        } */
        try {
            val fileInputStream = openFileInput("profileImg.png")
            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
            photoIV.setImageBitmap(b)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        fNameTV.text = savedInstanceState.getString(FULL_NAME_SAVE_KEY) ?: ""
        nicknameTV.text = savedInstanceState.getString(NICKNAME_SAVE_KEY) ?: ""
        emailTV.text = savedInstanceState.getString(EMAIL_SAVE_KEY) ?: ""
        locationTV.text = savedInstanceState.getString(LOCATION_SAVE_KEY) ?: ""
        skillsArray = savedInstanceState.getStringArrayList(SKILLS_SAVE_KEY) ?: arrayListOf()
        descriptionTV.text = savedInstanceState.getString(DESCRIPTION_SAVE_KEY) ?: ""

        for (skill in skillsArray) {
            for (chip in chipControl) {
                if (chip.text == skill)
                    chip.visibility = View.VISIBLE
            }
        }
    }

    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        //intent.putExtra(PHOTO_KEY, photoIV.drawable.toBitmap())
        intent.putExtra(FULL_NAME_KEY, fNameTV.text.toString())
        intent.putExtra(NICKNAME_KEY, nicknameTV.text.toString())
        intent.putExtra(EMAIL_KEY, emailTV.text.toString())
        intent.putExtra(LOCATION_KEY, locationTV.text.toString())
        intent.putExtra(SKILLS_KEY, skillsArray)
        intent.putExtra(DESCRIPTION_KEY, descriptionTV.text.toString())
        startActivityForResult(intent, EDIT_KEY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_KEY -> {
                if (resultCode == Activity.RESULT_OK) {
                   /* data?.getParcelableExtra<Bitmap>(PHOTO_KEY)?.let {
                        photoIV.setImageBitmap(it)
                        photo = it
                    }*/
                    try {
                        val fileInputStream = openFileInput("profileImg.png")
                        val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
                        photoIV.setImageBitmap(b)
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    }

                    val user = User(
                        data?.getStringExtra(FULL_NAME_KEY) ?: "",
                        data?.getStringExtra(NICKNAME_KEY) ?: "",
                        data?.getStringExtra(EMAIL_KEY) ?: "",
                        data?.getStringExtra(LOCATION_KEY) ?: "",
                        data?.getStringArrayListExtra(SKILLS_KEY) ?: arrayListOf(),
                        data?.getStringExtra(DESCRIPTION_KEY) ?: ""
                    )
                    fNameTV.text = user.fullName
                    nicknameTV.text = user.nickName
                    emailTV.text = user.email
                    locationTV.text = user.location
                    descriptionTV.text = user.description

                    skillsArray = (data?.getStringArrayListExtra(SKILLS_KEY) ?: arrayListOf())

                    for (chip in chipControl) {
                        chip.visibility = View.GONE
                    }

                    for (skill in skillsArray) {
                        for (chip in chipControl) {
                            if (chip.text == skill)
                                chip.visibility = View.VISIBLE
                        }
                    }

                    sharedPref?.edit()
                        ?.putString(PROFILE_KEY, Gson().toJson(user) ?: "")
                        ?.apply()


                }
            }
            else -> Unit
        }
    }
}
