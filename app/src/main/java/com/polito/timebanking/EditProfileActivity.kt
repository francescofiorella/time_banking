package com.polito.timebanking

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
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
import androidx.core.view.drawToBitmap
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.chip.Chip

class EditProfileActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar
    lateinit var photoIV: ImageView
    lateinit var button: ImageButton
    lateinit var fNameET: EditText
    lateinit var nicknameET: EditText
    lateinit var emailET: EditText
    lateinit var locationET: EditText
    lateinit var descriptionET : EditText


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


        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        button.setOnClickListener {
            showMenu(it, R.menu.insert_photo_menu)
        }

        photoIV.setImageBitmap(intent.getParcelableExtra(ShowProfileActivity.PHOTO_KEY))
        fNameET.setText(intent.getStringExtra(ShowProfileActivity.FULL_NAME_KEY) ?: "")
        nicknameET.setText(intent.getStringExtra(ShowProfileActivity.NICKNAME_KEY) ?: "")
        emailET.setText(intent.getStringExtra(ShowProfileActivity.EMAIL_KEY) ?: "")
        locationET.setText(intent.getStringExtra(ShowProfileActivity.LOCATION_KEY) ?: "")
        descriptionET.setText(intent.getStringExtra(ShowProfileActivity.DESCRIPTION_KEY)?: "")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(PHOTO_SAVE_KEY, photoIV.drawable.toBitmap())
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getParcelable<Bitmap>(PHOTO_SAVE_KEY)?.let {
            photoIV.setImageBitmap(it)
        }
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
            descriptionET.text.toString(), /* ******** AGGIUSTARE QUI ********** */
            descriptionET.text.toString()
        )
        intent.putExtra(ShowProfileActivity.FULL_NAME_KEY, user.fullName)
        intent.putExtra(ShowProfileActivity.NICKNAME_KEY, user.nickName)
        intent.putExtra(ShowProfileActivity.EMAIL_KEY, user.email)
        intent.putExtra(ShowProfileActivity.LOCATION_KEY, user.location)
        intent.putExtra(ShowProfileActivity.DESCRIPTION_KEY, user.description)
        intent.putExtra(ShowProfileActivity.PHOTO_KEY, photoIV.drawable.toBitmap())
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
        }
    }
}