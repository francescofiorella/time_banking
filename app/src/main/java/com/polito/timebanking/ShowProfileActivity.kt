package com.polito.timebanking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ShowProfileActivity : AppCompatActivity() {

    companion object {
        const val EDIT_KEY = 10
        const val FULL_NAME_KEY = "group36.lab1.FULL_NAME"
        const val NICKNAME_KEY = "group36.lab1.NICKNAME"
        const val EMAIL_KEY = "group36.lab1.EMAIL"
        const val LOCATION_KEY = "group36.lab1.LOCATION"
    }

    lateinit var toolbar: MaterialToolbar
    lateinit var fNameTV: TextView
    lateinit var nicknameTV: TextView
    lateinit var emailTV: TextView
    lateinit var locationTV: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_profile)

        toolbar = findViewById(R.id.topAppBar)
        fNameTV = findViewById(R.id.tv_fullname)
        nicknameTV = findViewById(R.id.tv_nickname)
        emailTV = findViewById(R.id.tv_email)
        locationTV = findViewById(R.id.tv_location)

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

    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        intent.putExtra(FULL_NAME_KEY, fNameTV.text.toString())
        intent.putExtra(NICKNAME_KEY, nicknameTV.text.toString())
        intent.putExtra(EMAIL_KEY, emailTV.text.toString())
        intent.putExtra(LOCATION_KEY, locationTV.text.toString())
        startActivityForResult(intent, EDIT_KEY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            EDIT_KEY -> {
                if (resultCode == Activity.RESULT_OK) {
                    fNameTV.text = data?.getStringExtra(FULL_NAME_KEY) ?: ""
                    nicknameTV.text = data?.getStringExtra(NICKNAME_KEY) ?: ""
                    emailTV.text = data?.getStringExtra(EMAIL_KEY) ?: ""
                    locationTV.text = data?.getStringExtra(LOCATION_KEY) ?: ""
                }
            }
            else -> Unit
        }
    }
}