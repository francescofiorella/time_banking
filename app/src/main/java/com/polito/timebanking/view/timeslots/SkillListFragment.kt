package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.R
import com.polito.timebanking.view.timeslots.TimeSlotListFragment.Companion.MODE_KEY
import com.polito.timebanking.view.timeslots.TimeSlotListFragment.Companion.SKILL_KEY
import com.polito.timebanking.view.timeslots.TimeSlotListFragment.Companion.SKILL_LIST
import com.polito.timebanking.databinding.FragmentSkillListBinding
import com.polito.timebanking.models.Skill
import com.polito.timebanking.utils.SkillAdapter
import com.polito.timebanking.utils.SkillListener
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.SkillViewModel

class SkillListFragment : Fragment(), SkillListener {

    private lateinit var binding: FragmentSkillListBinding
    private val viewModel by viewModels<SkillViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_skill_list, container, false)

        viewModel.skillList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.noSkillsTv.isVisible = true
                binding.listRecyclerView.isVisible = false
            } else {
                binding.noSkillsTv.isVisible = false
                binding.listRecyclerView.isVisible = true
                SkillAdapter(list, this).also {
                    binding.listRecyclerView.adapter = it
                }
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                (activity as MainActivity).getDrawerLayout().open()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onClickListener(skill: Skill, position: Int) {
        // pass skill id
        val bundle = bundleOf(MODE_KEY to SKILL_LIST, SKILL_KEY to skill.sid)
        findNavController().navigate(R.id.action_skillListFragment_to_timeSlotListFragment, bundle)
    }
}