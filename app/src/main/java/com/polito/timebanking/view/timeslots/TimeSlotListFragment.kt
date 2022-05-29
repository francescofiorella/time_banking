package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.*
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentTimeSlotListBinding
import com.polito.timebanking.models.TimeSlot
import com.polito.timebanking.utils.TimeSlotAdapter
import com.polito.timebanking.utils.TimeSlotListener
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.viewmodels.TimeSlotViewModel
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.ADD_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.EDIT_MODE
import com.polito.timebanking.viewmodels.TimeSlotViewModel.Companion.NONE

class TimeSlotListFragment : Fragment(), TimeSlotListener {
    private lateinit var binding: FragmentTimeSlotListBinding
    private val viewModel by activityViewModels<TimeSlotViewModel>()

    companion object {
        const val SKILL_KEY = "skill_key"
        const val MODE_KEY = "mode_key"
        const val MY_LIST = 1
        const val SKILL_LIST = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_time_slot_list, container, false)

        if (savedInstanceState == null) {
            viewModel.resetList()
            viewModel.listFragmentMode = arguments?.getInt(MODE_KEY) ?: MY_LIST
            viewModel.sid = arguments?.getString(SKILL_KEY) ?: ""
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.loadingCpi.isVisible = isLoading
            binding.listRecyclerView.isVisible = !isLoading
        }

        viewModel.timeSlotList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.noTimeSlotsTv.isVisible = true
                binding.listRecyclerView.isVisible = false
            } else {
                binding.noTimeSlotsTv.isVisible = false
                binding.listRecyclerView.isVisible = true
                TimeSlotAdapter(list, this).also {
                    binding.listRecyclerView.adapter = it
                }
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            getDrawerLayout().setDrawerLockMode(
                if (viewModel.listFragmentMode == MY_LIST) {
                    DrawerLayout.LOCK_MODE_UNLOCKED
                } else {
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                }
            )
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addFab.setOnClickListener {
            viewModel.editFragmentMode = ADD_MODE
            viewModel.currentTimeSlot = TimeSlot()
            findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(
            if (viewModel.listFragmentMode == MY_LIST) {
                R.drawable.ic_menu
            } else {
                R.drawable.ic_arrow_back
            }
        )
        viewModel.editFragmentMode = NONE
        viewModel.currentTimeSlot = null
        viewModel.loadList()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.list_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchET = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchET.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary))
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val adapter = binding.listRecyclerView.adapter as TimeSlotAdapter
                adapter.filter.filter(newText)
                return true
            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val adapter = binding.listRecyclerView.adapter as TimeSlotAdapter?

        return when (item.itemId) {
            android.R.id.home -> {
                if (viewModel.listFragmentMode == MY_LIST) {
                    (activity as MainActivity).getDrawerLayout().open()
                } else {
                    activity?.onBackPressed()
                }
                true
            }

            R.id.title_sort -> {
                adapter?.sortByTitle()
                true
            }

            R.id.date_sort -> {
                adapter?.sortByDate()
                true
            }

            R.id.half_hour -> {
                adapter?.filterDuration(getString(R.string.half_hour))
                true
            }

            R.id.one_hour -> {
                adapter?.filterDuration(getString(R.string.one_hour))
                true
            }

            R.id.two_hour -> {
                adapter?.filterDuration(getString(R.string.two_hour))
                true
            }

            R.id.three_hour -> {
                adapter?.filterDuration(getString(R.string.three_hour))
                true
            }

            R.id.more_three_hour -> {
                adapter?.filterDuration(getString(R.string.more_three_hour))
                true
            }

            R.id.no_filter -> {
                adapter?.clearFilter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCardClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = NONE
        viewModel.setTimeSlot(timeSlot)
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotDetailsFragment)
    }

    override fun onEditClickListener(timeSlot: TimeSlot, position: Int) {
        viewModel.editFragmentMode = EDIT_MODE
        viewModel.setTimeSlot(timeSlot)
        findNavController().navigate(R.id.action_timeSlotListFragment_to_timeSlotEditFragment)
    }
}