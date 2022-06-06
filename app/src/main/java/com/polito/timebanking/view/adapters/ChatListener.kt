package com.polito.timebanking.view.adapters

import com.polito.timebanking.models.Chat

interface ChatListener {
    fun onChatClickListener(chat: Chat, position: Int)
}