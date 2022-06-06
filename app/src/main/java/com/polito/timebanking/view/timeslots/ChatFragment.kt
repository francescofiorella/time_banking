package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentChatBinding
import com.polito.timebanking.view.adapters.ChatMessageAdapter
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.TIMESLOT_ID_KEY
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.USER_ID_KEY
import com.polito.timebanking.viewmodels.ChatViewModel

class ChatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private val viewModel by viewModels<ChatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chat, container, false)

        viewModel.tsid = arguments?.getString(TIMESLOT_ID_KEY) ?: ""
        viewModel.uid = arguments?.getString(USER_ID_KEY) ?: ""
        viewModel.loadMessages()

        viewModel.chatMessageList.observe(viewLifecycleOwner) { messageList ->
            if (messageList.isEmpty()) {
                binding.requestLayout.isVisible = false
                binding.listRecyclerView.isVisible = false
                binding.noMessagesTv.isVisible = true
            } else {
                binding.noMessagesTv.isVisible = false
                binding.listRecyclerView.isVisible = true
                (binding.listRecyclerView.layoutManager as LinearLayoutManager?)
                    ?.stackFromEnd = true
                viewModel.getLoggedUserId()?.let { uid ->
                    ChatMessageAdapter(messageList, uid).also {
                        binding.listRecyclerView.adapter = it
                    }
                }
            }
            viewModel.timeSlot.observe(viewLifecycleOwner) {
                if (binding.listRecyclerView.isVisible) {
                    if (viewModel.isChatMine()) {
                        binding.requestLayout.isVisible = true
                        if (viewModel.isTimeSlotAvailable()) {
                            binding.acceptBtn.setOnClickListener {
                                viewModel.acceptTimeSlot()
                                binding.acceptBtn.isVisible = false
                                binding.requestTv.textAlignment = View.TEXT_ALIGNMENT_CENTER
                                binding.requestTv.text = getString(R.string.request_accepted)
                            }
                        } else {
                            binding.acceptBtn.isVisible = false
                            binding.requestTv.textAlignment = View.TEXT_ALIGNMENT_CENTER
                            binding.requestTv.text = getString(R.string.request_accepted)
                        }
                    } else {
                        binding.requestLayout.isVisible = false
                    }
                }
            }
        }

        binding.sendFab.setOnClickListener {
            val text = binding.writeEt.text.toString()
            if (text.isNotEmpty()) {
                binding.writeEt.setText("")
                viewModel.sendMessage(text, isChatOpened = binding.listRecyclerView.isVisible)
            }
        }

        viewModel.otherUser.observe(viewLifecycleOwner) { user ->
            (activity as MainActivity).apply {
                supportActionBar?.title = user.fullName ?: getString(R.string.app_name)
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