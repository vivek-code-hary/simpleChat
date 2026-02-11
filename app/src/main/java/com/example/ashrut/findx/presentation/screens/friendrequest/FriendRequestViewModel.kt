package com.example.ashrut.findx.presentation.screens.friendrequest

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
import com.google.firebase.firestore.ListenerRegistration


class FriendRequestViewModel : ViewModel() {

    private val friendRepo = FriendRepository()
    private val userRepo = UserRepository()

    var receivedRequests by mutableStateOf<List<User>>(emptyList())
        private set

    private var currentUserListener: ListenerRegistration? = null
    private var requestUsersListener: ListenerRegistration? = null

    init {
        observeReceivedRequests()
    }

    private fun observeReceivedRequests() {

        // ✅ STEP 1: listen CURRENT user (single document)
        currentUserListener = userRepo.listenCurrentUser(
            onResult = { user ->

                if (user == null) {
                    receivedRequests = emptyList()
                    requestUsersListener?.remove()
                    return@listenCurrentUser
                }

                // remove old listener
                requestUsersListener?.remove()

                // ✅ STEP 2: listen REQUEST USERS (multiple docs)
                if (user.requestsReceived.isEmpty()) {
                    receivedRequests = emptyList()
                } else {
                    requestUsersListener = userRepo.listenUsersByIds(
                        ids = user.requestsReceived
                    ) { users ->
                        receivedRequests = users
                    }
                }
            },
            onError = {
                println("❌ Error listening current user: $it")
            }
        )
    }

    fun accept(fromUserId: String) {
        viewModelScope.launch {
            val myId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            friendRepo.acceptRequest(myId, fromUserId)
        }
    }

    fun reject(fromUserId: String) {
        viewModelScope.launch {
            val myId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            friendRepo.rejectRequest(myId, fromUserId)
        }
    }

    override fun onCleared() {
        currentUserListener?.remove()
        requestUsersListener?.remove()
        super.onCleared()
    }
}
