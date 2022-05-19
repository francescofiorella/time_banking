package com.polito.timebanking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.databinding.FragmentSkillListBinding
import com.polito.timebanking.models.Skill
import com.polito.timebanking.utils.SkillAdapter
import com.polito.timebanking.utils.SkillListener
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

        return binding.root
    }

    override fun onClickListener(skill: Skill, position: Int) {
        // pass skill id
        findNavController().navigate(R.id.action_skillListFragment_to_timeSlotListFragment)
    }
}