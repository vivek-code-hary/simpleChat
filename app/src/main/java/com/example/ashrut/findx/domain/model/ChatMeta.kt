package com.example.ashrut.findx.domain.model

data class ChatMeta(
    val chatId: String = "",
    val userIds: List<String> = emptyList(),
    val lastMessage: String = "",
    val lastMessageSenderId: String = "" ,//
    val lastTimestamp: Long = 0L,
    val lastMessageDelivered: Boolean = false,
    val lastMessageSeen: Boolean = false,
    val unreadCount: Map<String, Int> = emptyMap()
)
