package com.polito.timebanking.view

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.polito.timebanking.R
import com.polito.timebanking.utils.snackBar
import com.polito.timebanking.view.timeslots.TimeSlotListFragment
import com.polito.timebanking.viewmodels.UserViewModel

class MainActivity : AppCompatActivity() {
    private val userModel by viewModels<UserViewModel>()

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setupWithNavController(navController!!)

        setSupportActionBar(findViewById<MaterialToolbar>(R.id.toolbar))

        userModel.currentUser.observe(this) { currentUser ->
            Log.d(
                "MainActivity",
                "userModel.currentUser.observe (currentUser = ${currentUser})"
            )

            val header = navView.getHeaderView(0)
            val fullNameTV = header.findViewById<TextView>(R.id.tv_full_name)
            fullNameTV.text = currentUser?.fullName
            val emailTV = header.findViewById<TextView>(R.id.tv_email)
            emailTV.text = currentUser?.email
            val timeCreditValueTV = header.findViewById<TextView>(R.id.tv_time_credit_value)
            val timeCreditValue = "${currentUser?.timeCredit ?: 0} ${if (currentUser?.timeCredit == 1) "hour" else "hours"}"
            timeCreditValueTV.text = timeCreditValue

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

        navView.setNavigationItemSelectedListener { item ->
            val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout.closeDrawer(GravityCompat.START)
            when (item.itemId) {
                R.id.authFragment -> {
                    userModel.signOut()
                    navController?.apply {
                        // reset the navGraph (the start destination)
                        graph = navInflater.inflate(R.navigation.navigation_graph)
                    }
                }

                R.id.timeSlotRequiredListFragment -> {
                    val bundle = bundleOf(TimeSlotListFragment.FROM to "REQUIRED")
                    navController?.navigate(R.id.timeSlotListFragment, bundle)
                }

                R.id.mytimeSlotListFragment -> {
                    val bundle = bundleOf(TimeSlotListFragment.FROM to null)
                    navController?.navigate(R.id.timeSlotListFragment, bundle)
                }

                else -> navController?.navigate(item.itemId)
            }
            true
        }
    }

    fun getDrawerLayout(): DrawerLayout = findViewById(R.id.drawer_layout)

    fun setNavCheckedItem(@IdRes id: Int) {
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.menu.findItem(id).isChecked = true
    }
}