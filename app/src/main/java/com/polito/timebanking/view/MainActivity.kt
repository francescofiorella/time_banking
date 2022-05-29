package com.polito.timebanking.view

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.polito.timebanking.R
import com.polito.timebanking.databinding.ActivityMainBinding
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    private val userModel by viewModels<UserViewModel>()

    private lateinit var binding: ActivityMainBinding
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        binding.navView.setupWithNavController(navController!!)

        setSupportActionBar(binding.toolbar)

        userModel.currentUser.observe(this) { currentUser ->
            Log.d(
                "MainActivity",
                "userModel.currentUser.observe (currentUser = ${currentUser})"
            )

            val header = binding.navView.getHeaderView(0)
            val fullNameTV = header.findViewById<TextView>(R.id.tv_full_name)
            fullNameTV.text = currentUser?.fullName
            val emailTV = header.findViewById<TextView>(R.id.tv_email)
            emailTV.text = currentUser?.email

            val photoIV = header.findViewById<ImageView>(R.id.iv_photo)
            if (currentUser?.photoUrl.isNullOrEmpty()) {
                photoIV.setImageDrawable(
                    ContextCompat.getDrawable(
                        photoIV.context,
                        R.drawable.ic_user
                    )
                )
            } else {
                Glide.with(photoIV)
                    .load(currentUser?.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(photoIV)
            }
        }

        userModel.errorMessage.observe(this) { errorMessage ->
            Log.d(
                "MainActivity",
                "userModel.errorMessage.observe (errorMessage = ${errorMessage})"
            )
            if (errorMessage != "") {
                this.snackBar(
                    errorMessage,
                    length = BaseTransientBottomBar.LENGTH_LONG,
                    isDismissible = true
                )
                userModel.errorMessage.value = ""
            }
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.authFragment) {
                userModel.signOut()
                navController?.apply {
                    // reset the navGraph (the start destination)
                    graph = navInflater.inflate(R.navigation.navigation_graph)
                }
            } else {
                NavigationUI.onNavDestinationSelected(item, navController!!)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    fun getDrawerLayout() = binding.drawerLayout
}