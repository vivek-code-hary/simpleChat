package com.example.ashrut.findx.domain.model

import com.google.firebase.Timestamp

data class Message(
    val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val text: String = "",
    val replyTo: String? = null,
    val timestamp: Timestamp = Timestamp.now(),

    val isDelivered: Boolean = false,
    val isSeen: Boolean = false
)

