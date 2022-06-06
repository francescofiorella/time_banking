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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentReviewListBinding
import com.polito.timebanking.models.Feedback
import com.polito.timebanking.view.adapters.ReviewAdapter
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.view.profile.ShowProfileFragment
import com.polito.timebanking.viewmodels.FeedbackViewModel
import com.polito.timebanking.viewmodels.UserViewModel

class ReviewListFragment : Fragment() {
    private lateinit var binding: FragmentReviewListBinding
    private val viewModel by activityViewModels<FeedbackViewModel>()
    private val userModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_review_list, container, false)
        viewModel.feedbackList.observe(viewLifecycleOwner) { list ->
            var listFiltered = emptyList<Feedback>()
            arguments?.getString(ShowProfileFragment.USER_ID_KEY)?.let { uid ->
                listFiltered = list.filter {
                    it.destuid == uid
                }
            }
            if (listFiltered.isEmpty()) {
                binding.noReviewsTv.isVisible = true
                binding.listRecyclerView.isVisible = false
            } else {
                binding.noReviewsTv.isVisible = false
                binding.listRecyclerView.isVisible = true
                ReviewAdapter(listFiltered).also {
                    binding.listRecyclerView.adapter = it
                }
            }
        }
        binding.user = userModel.user
        val user = userModel.user

        if (binding.photoIv != null) {
            if (user.value?.photoUrl.isNullOrEmpty()) {
                binding.photoIv?.setImageDrawable(
                    ContextCompat.getDrawable(
                        binding.photoIv!!.context,
                        R.drawable.ic_user
                    )
                )
            } else {
                Glide.with(binding.photoIv!!)
                    .load(user.value?.photoUrl)
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.photoIv!!)
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = "Reviews"
            getDrawerLayout().setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.review_menu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView
        val searchET = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchET.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary))
        searchView.maxWidth = Int.MAX_VALUE
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val adapter = binding.listRecyclerView.adapter as ReviewAdapter
                adapter.filter.filter(newText)
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val adapter = binding.listRecyclerView.adapter as ReviewAdapter?

        return when (item.itemId) {
            android.R.id.home -> {
                activity?.onBackPressed()
                true
            }

            R.id.rate_sort -> {
                adapter?.sortByRate()
                true
            }

            R.id.one_star -> {
                adapter?.filterRate(1)
                true
            }

            R.id.two_stars -> {
                adapter?.filterRate(2)
                true
            }

            R.id.three_stars -> {
                adapter?.filterRate(3)
                true
            }

            R.id.four_stars -> {
                adapter?.filterRate(4)
                true
            }

            R.id.five_stars -> {
                adapter?.filterRate(5)
                true
            }

            R.id.no_filter -> {
                adapter?.clearFilter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}