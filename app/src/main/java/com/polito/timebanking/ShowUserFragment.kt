package com.polito.timebanking

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import com.polito.timebanking.databinding.FragmentShowUserBinding
import com.polito.timebanking.viewmodels.UserViewModel

class ShowUserFragment : Fragment() {
    private val userModel by activityViewModels<UserViewModel>()

    private lateinit var binding: FragmentShowUserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_show_user, container, false)

        binding.viewmodel = userModel

        binding.lifecycleOwner = this.viewLifecycleOwner

        userModel.user.observe(viewLifecycleOwner) { user ->
            Log.d(
                "ShowUserFragment",
                "userModel.user.observe (user = ${user})"
            )

            binding.skillsCg.removeAllViews()
            user?.skills?.forEach { skill ->
                val chip = layoutInflater.inflate(
                    R.layout.layout_skill_chip,
                    binding.skillsCg,
                    false
                ) as Chip
                chip.isCheckable = false
                chip.text = skill.name
                binding.skillsCg.addView(chip)
            }
        }

        userModel.userBitmap.observe(viewLifecycleOwner) { userBitmap ->
            Log.d(
                "ShowUserFragment",
                "userModel.userBitmap.observe (userBitmap = ${userBitmap})"
            )
            if (userBitmap != null) {
                binding.ivPhoto.setImageBitmap(userBitmap)
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}