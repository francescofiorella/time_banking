package com.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.polito.timebanking.databinding.ActivityMainBinding
import com.polito.timebanking.utils.loadBitmapFromStorage
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

        userModel.currentUserBitmap.observe(this) {
            Log.d("DEBUG", "MainActivity - userModel.currentUserBitmap.observe - $it")
            if (it != null) {
                val header = binding.navView.getHeaderView(0)
                val photoIV = header.findViewById<ImageView>(R.id.iv_photo)
                photoIV.setImageBitmap(it)
            }
        }

        userModel.currentUser.observe(this) {
            Log.d("DEBUG", "MainActivity - userModel.currentUser.observe - $it")
//            if (userModel.currentUserBitmap.value == null) {
//                it?.photoPath?.let { photoPath ->
//                    loadBitmapFromStorage(
//                        applicationContext,
//                        photoPath
//                    )
//                }.let { bitmap ->
//                    userModel.currentUserBitmap.value = bitmap
//                }
//            }

            val header = binding.navView.getHeaderView(0)
            val fullNameTV = header.findViewById<TextView>(R.id.tv_full_name)
            fullNameTV.text = it?.fullName
            val emailTV = header.findViewById<TextView>(R.id.tv_email)
            emailTV.text = it?.email
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    when (navController?.currentDestination?.id) {
                        R.id.timeSlotDetailsFragment -> {
                            (navHostFragment.childFragmentManager.findFragmentById(
                                R.id.nav_host_fragment_content_main
                            ) as TimeSlotDetailsFragment).navigateToEdit()
                            true
                        }
                        R.id.showProfileFragment -> {
                            navController?.navigate(R.id.action_showProfileFragment_to_editProfileFragment)
                            true
                        }
                        else -> false
                    }
                }
                else -> false
            }
        }

        binding.navView.setNavigationItemSelectedListener { item ->
            if (item.itemId == R.id.signInFragment) {
                Log.d("DEBUG", "MainActivity - binding.navView.setNavigationItemSelectedListener")
                userModel.signOut()
                navController?.navigate(R.id.signInFragment)
            } else {
                NavigationUI.onNavDestinationSelected(item, navController!!)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private val navigationListener = NavController.OnDestinationChangedListener { _, _, _ ->
        onPrepareOptionsMenu(binding.toolbar.menu)
    }

    override fun onResume() {
        super.onResume()
        navController?.addOnDestinationChangedListener(navigationListener)
    }

    override fun onPause() {
        super.onPause()
        navController?.removeOnDestinationChangedListener(navigationListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)
        menu?.findItem(R.id.edit)?.isVisible = when (navController?.currentDestination?.id) {
            R.id.timeSlotDetailsFragment -> true
            R.id.showProfileFragment -> true
            else -> false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            when (navController?.currentDestination?.id) {
                R.id.timeSlotListFragment, R.id.showProfileFragment -> binding.drawerLayout.open()
                else -> onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}