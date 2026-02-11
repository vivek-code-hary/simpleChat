package com.example.ashrut.findx.presentation.screens.users

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashrut.findx.data.repository.FriendRepository
import com.example.ashrut.findx.data.repository.UserRepository
import com.example.ashrut.findx.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {

    private val userRepo = UserRepository()
    private val friendRepo = FriendRepository()
    private val auth = FirebaseAuth.getInstance()

    var users by mutableStateOf<List<User>>(emptyList())
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    init {
        observeCurrentUser()
        observeUsers()
    }

    private fun observeCurrentUser() {
        val uid = auth.currentUser?.uid ?: return
        userRepo.listenCurrentUser(
            onResult = { currentUser = it },
            onError = { }
        )
    }

    private fun observeUsers() {
        userRepo.listenAllUsers(
            onResult = { list ->
                users = list.filter { it.uid != auth.currentUser?.uid }
            },
            onError = {}
        )
    }

    fun onSearchChange(text: String) {
        searchQuery = text
    }

    fun sendRequest(toId: String) {
        val myId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepo.sendRequest(myId, toId)
        }
    }

    fun cancelRequest(toId: String) {
        val myId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            friendRepo.rejectRequest(myId, toId)
        }
    }

    val filteredUsers: List<User>
        get() = if (searchQuery.isBlank()) {
            users
        } else {
            users.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
        }
}
