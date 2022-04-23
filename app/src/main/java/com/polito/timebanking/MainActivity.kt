package com.polito.timebanking

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import com.polito.timebanking.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

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
        binding.toolbar.setNavigationOnClickListener {
            binding.drawerLayout.open()
        }

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.edit -> {
                    navController?.navigate(R.id.action_timeSlotDetailsFragment_to_timeSlotEditFragment)
                    true
                }
                else -> false
            }
        }

        /*appBarConfiguration = AppBarConfiguration(
            setOf(R.id.timeSlotListFragment), binding.drawerLayout
        )*/
        //setupActionBarWithNavController(navController, appBarConfiguration)
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
        menu?.findItem(R.id.edit)?.isVisible = navController?.currentDestination?.id == R.id.timeSlotDetailsFragment
        return true
    }

    /*override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }*/
}