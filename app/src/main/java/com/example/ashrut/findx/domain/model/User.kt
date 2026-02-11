package com.example.ashrut.findx.domain.model

data class User(
    val uid : String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val friends: List<String> = emptyList(),
    val requestsSent: List<String> = emptyList(),
    val requestsReceived: List<String> = emptyList()
)
