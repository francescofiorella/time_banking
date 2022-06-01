package com.polito.timebanking.models

data class ChatMessage(
    val from: String? = null,
    val to: String? = null,
    val body: String? = null,
    val timestamp: Long? = null
)