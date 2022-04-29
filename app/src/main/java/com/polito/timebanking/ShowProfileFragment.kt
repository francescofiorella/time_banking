package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.polito.timebanking.databinding.FragmentShowProfileBinding
import com.polito.timebanking.viewmodels.UserViewModel

class ShowProfileFragment : Fragment(R.layout.fragment_show_profile) {

    private lateinit var binding: FragmentShowProfileBinding
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_show_profile, container, false)
        binding.user = viewModel.user

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        return binding.root
    }

//    companion object {
//        const val EDIT_KEY = 10
//        const val FULL_NAME_KEY = "group36.lab1.FULL_NAME"
//        const val NICKNAME_KEY = "group36.lab1.NICKNAME"
//        const val EMAIL_KEY = "group36.lab1.EMAIL"
//        const val LOCATION_KEY = "group36.lab1.LOCATION"
//        const val SKILLS_KEY = "group36.lab1.SKILLS"
//        const val DESCRIPTION_KEY = "group36.lab1.DESCRIPTION"
//
//        const val PATH_PHOTO = "profileImg.png"
//
//        private const val FULL_NAME_SAVE_KEY = "fullName_save_key"
//        private const val NICKNAME_SAVE_KEY = "nickname_save_key"
//        private const val EMAIL_SAVE_KEY = "email_save_key"
//        private const val LOCATION_SAVE_KEY = "location_save_key"
//        private const val SKILLS_SAVE_KEY = "skills_save_key"
//        private const val DESCRIPTION_SAVE_KEY = "description_save_key"
//
//        private const val SHARED_KEY = "shared_key"
//        private const val PROFILE_KEY = "profile"
//    }
//
//    private lateinit var toolbar: MaterialToolbar
//    private lateinit var photoIV: ImageView
//    private lateinit var fNameTV: TextView
//    private lateinit var nicknameTV: TextView
//    private lateinit var emailTV: TextView
//    private lateinit var locationTV: TextView
//    private lateinit var skillsCG: ChipGroup
//    private lateinit var descriptionTV: TextView
//    private var skillsList = arrayListOf<String>()
//    private var sharedPref: SharedPreferences? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_show_profile)
//
//        toolbar = findViewById(R.id.toolbar)
//        photoIV = findViewById(R.id.iv_photo)
//        fNameTV = findViewById(R.id.tv_full_name)
//        nicknameTV = findViewById(R.id.tv_nickname)
//        emailTV = findViewById(R.id.tv_email)
//        locationTV = findViewById(R.id.tv_location)
//        skillsCG = findViewById(R.id.skills_cg)
//        descriptionTV = findViewById(R.id.tv_description)
//
//        sharedPref = applicationContext?.getSharedPreferences(SHARED_KEY, Context.MODE_PRIVATE)
//
//        if (savedInstanceState == null) {
//            sharedPref?.getString(PROFILE_KEY, "")?.let {
//                if (it != "") {
//                    val user = Gson().fromJson(it, User::class.java)
//                    fNameTV.text = user.fullName
//                    nicknameTV.text = user.nickname
//                    emailTV.text = user.email
//                    locationTV.text = user.location
//                    skillsList = user.skills
//                    descriptionTV.text = user.description
//                }
//            }
//        }
//
//        setImageFromStorage()
//
//        for (idx in 0 until skillsCG.childCount) {
//            val skillC: Chip = skillsCG.getChildAt(idx) as Chip
//            if (skillsList.contains(skillC.text.toString())) {
//                skillC.visibility = View.VISIBLE
//            }
//        }
//
//        toolbar.setOnMenuItemClickListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.edit -> {
//                    editProfile()
//                    true
//                }
//
//                else -> false
//            }
//        }
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putString(FULL_NAME_SAVE_KEY, fNameTV.text.toString())
//        outState.putString(NICKNAME_SAVE_KEY, nicknameTV.text.toString())
//        outState.putString(EMAIL_SAVE_KEY, emailTV.text.toString())
//        outState.putString(LOCATION_SAVE_KEY, locationTV.text.toString())
//        outState.putStringArrayList(SKILLS_SAVE_KEY, skillsList)
//        outState.putString(DESCRIPTION_SAVE_KEY, descriptionTV.text.toString())
//    }
//
//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        try {
//            val fileInputStream = openFileInput(PATH_PHOTO)
//            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
//            photoIV.setImageBitmap(b)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//        fNameTV.text = savedInstanceState.getString(FULL_NAME_SAVE_KEY) ?: ""
//        nicknameTV.text = savedInstanceState.getString(NICKNAME_SAVE_KEY) ?: ""
//        emailTV.text = savedInstanceState.getString(EMAIL_SAVE_KEY) ?: ""
//        locationTV.text = savedInstanceState.getString(LOCATION_SAVE_KEY) ?: ""
//        skillsList = savedInstanceState.getStringArrayList(SKILLS_SAVE_KEY) ?: arrayListOf()
//        descriptionTV.text = savedInstanceState.getString(DESCRIPTION_SAVE_KEY) ?: ""
//
//        for (idx in 0 until skillsCG.childCount) {
//            val skillC: Chip = skillsCG.getChildAt(idx) as Chip
//            if (skillsList.contains(skillC.text.toString())) {
//                skillC.visibility = View.VISIBLE
//            }
//        }
//    }
//
//    private fun editProfile() {
//        val intent = Intent(this, EditProfileActivity::class.java)
//        intent.putExtra(FULL_NAME_KEY, fNameTV.text.toString())
//        intent.putExtra(NICKNAME_KEY, nicknameTV.text.toString())
//        intent.putExtra(EMAIL_KEY, emailTV.text.toString())
//        intent.putExtra(LOCATION_KEY, locationTV.text.toString())
//        intent.putExtra(SKILLS_KEY, skillsList)
//        intent.putExtra(DESCRIPTION_KEY, descriptionTV.text.toString())
//        startActivityForResult(intent, EDIT_KEY)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            EDIT_KEY -> {
//                if (resultCode == Activity.RESULT_OK) {
//                    setImageFromStorage()
//
//                    val user = User(
//                        data?.getStringExtra(FULL_NAME_KEY) ?: "",
//                        data?.getStringExtra(NICKNAME_KEY) ?: "",
//                        data?.getStringExtra(EMAIL_KEY) ?: "",
//                        data?.getStringExtra(LOCATION_KEY) ?: "",
//                        data?.getStringArrayListExtra(SKILLS_KEY) ?: arrayListOf(),
//                        data?.getStringExtra(DESCRIPTION_KEY) ?: ""
//                    )
//                    fNameTV.text = user.fullName
//                    nicknameTV.text = user.nickname
//                    emailTV.text = user.email
//                    locationTV.text = user.location
//                    descriptionTV.text = user.description
//
//                    skillsList = (data?.getStringArrayListExtra(SKILLS_KEY) ?: arrayListOf())
//
//                    for (idx in 0 until skillsCG.childCount) {
//                        val skillC: Chip = skillsCG.getChildAt(idx) as Chip
//                        if (skillsList.contains(skillC.text.toString())) {
//                            skillC.visibility = View.VISIBLE
//                        } else {
//                            skillC.visibility = View.GONE
//                        }
//                    }
//
//                    sharedPref?.edit()
//                        ?.putString(PROFILE_KEY, Gson().toJson(user) ?: "")
//                        ?.apply()
//
//
//                }
//            }
//            else -> Unit
//        }
//    }
//
//    private fun setImageFromStorage() {
//        try {
//            val fileInputStream = openFileInput(PATH_PHOTO)
//            val b: Bitmap = BitmapFactory.decodeStream(fileInputStream)
//            photoIV.setImageBitmap(b)
//        } catch (e: FileNotFoundException) {
//            e.printStackTrace()
//        }
//    }
}