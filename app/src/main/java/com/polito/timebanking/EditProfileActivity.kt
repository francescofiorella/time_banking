package com.polito.timebanking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.appbar.MaterialToolbar

class EditProfileActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar
    lateinit var button: ImageButton
    lateinit var fNameET: EditText
    lateinit var nicknameET: EditText
    lateinit var emailET: EditText
    lateinit var locationET: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        toolbar = findViewById(R.id.topAppBar)
        button = findViewById(R.id.edit_photo)
        fNameET = findViewById(R.id.fullName_et)
        nicknameET = findViewById(R.id.nickname_et)
        emailET = findViewById(R.id.email_et)
        locationET = findViewById(R.id.location_et)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        button.setOnClickListener {
            showMenu(it, R.menu.insert_photo_menu)
        }

        fNameET.setText(intent.getStringExtra(ShowProfileActivity.FULL_NAME_KEY) ?: "")
        nicknameET.setText(intent.getStringExtra(ShowProfileActivity.NICKNAME_KEY) ?: "")
        emailET.setText(intent.getStringExtra(ShowProfileActivity.EMAIL_KEY) ?: "")
        locationET.setText(intent.getStringExtra(ShowProfileActivity.LOCATION_KEY) ?: "")
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(v.context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.select_image -> {
                    // Respond to context menu item 1 click.
                    true
                }
                R.id.take_image -> {
                    // Respond to context menu item 2 click.
                    true
                }
                else -> false
            }
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
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
        intent.putExtra(ShowProfileActivity.FULL_NAME_KEY, fNameET.text.toString())
        intent.putExtra(ShowProfileActivity.NICKNAME_KEY, nicknameET.text.toString())
        intent.putExtra(ShowProfileActivity.EMAIL_KEY, emailET.text.toString())
        intent.putExtra(ShowProfileActivity.LOCATION_KEY, locationET.text.toString())
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
    }
}