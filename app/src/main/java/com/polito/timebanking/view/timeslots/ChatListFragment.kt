package com.polito.timebanking.view.timeslots

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.polito.timebanking.R
import com.polito.timebanking.databinding.FragmentChatListBinding
import com.polito.timebanking.models.Chat
import com.polito.timebanking.utils.ChatAdapter
import com.polito.timebanking.utils.ChatListener
import com.polito.timebanking.view.MainActivity
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.TIMESLOT_ID_KEY
import com.polito.timebanking.view.timeslots.TimeSlotDetailsFragment.Companion.USER_ID_KEY
import com.polito.timebanking.viewmodels.ChatViewModel

class ChatListFragment : Fragment(), ChatListener {

    private lateinit var binding: FragmentChatListBinding
    private val viewModel by viewModels<ChatViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil
            .inflate(inflater, R.layout.fragment_chat_list, container, false)

        viewModel.loadChats()

        viewModel.chatList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.recView.isVisible = false
                binding.noChatsTv.isVisible = true
            } else {
                binding.recView.isVisible = true
                binding.noChatsTv.isVisible = false
                viewModel.getLoggedUserId()?.let {
                    binding.recView.adapter = ChatAdapter(list, it, this)
                }
            }
        }

        (activity as MainActivity).apply {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            supportActionBar?.title = getString(R.string.chats)
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

    override fun onChatClickListener(chat: Chat, position: Int) {
        val bundle = bundleOf(
            TIMESLOT_ID_KEY to chat.tsid,
            USER_ID_KEY to (chat.uids?.get(1) ?: "")
        )
        findNavController().navigate(
            R.id.action_chatListFragment_to_chatFragment,
            bundle
        )
    }
}