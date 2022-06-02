package com.polito.timebanking.utils

import com.polito.timebanking.models.Chat

interface ChatListener {
    fun onChatClickListener(chat: Chat, position: Int)
}