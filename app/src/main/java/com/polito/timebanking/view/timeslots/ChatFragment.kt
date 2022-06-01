package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentChatBinding
import com.polito.timebanking.utils.ChatMessageAdapter
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.TIMESLOT_ID_KEY
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
        viewModel.loadMessages()

        viewModel.chatMessageList.observe(viewLifecycleOwner) { messageList ->
            (binding.listRecyclerView.layoutManager as LinearLayoutManager?)?.stackFromEnd = true
            viewModel.getLoggedUserId()?.let { uid ->
                ChatMessageAdapter(messageList, uid).also {
                    binding.listRecyclerView.adapter = it
                }
            }
        }

        binding.sendFab.setOnClickListener {
            val text = binding.writeEt.text.toString()
            binding.writeEt.setText("")
            viewModel.sendMessage(text)
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