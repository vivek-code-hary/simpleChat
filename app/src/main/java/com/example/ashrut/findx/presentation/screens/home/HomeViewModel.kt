package com.example.ashrut.findx.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ashrut.findx.data.repository.ChatRepository
import com.example.ashrut.findx.data.repository.UserRepository
import com.example.ashrut.findx.domain.model.ChatMeta
import com.example.ashrut.findx.domain.model.User
import com.google.firebase.firestore.ListenerRegistration

class HomeViewModel : ViewModel() {

    private val userRepo = UserRepository()
    private val chatRepo = ChatRepository()

    var friends by mutableStateOf<List<User>>(emptyList())
        private set

    var chats by mutableStateOf<List<ChatMeta>>(emptyList())
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val myId: String?
        get() = currentUser?.uid

    private var userListener: ListenerRegistration? = null
    private var friendsListener: ListenerRegistration? = null  // ✅ NEW
    private var chatListener: ListenerRegistration? = null

    init {
        observeCurrentUser()
    }

    private fun observeCurrentUser() {
        userListener = userRepo.listenCurrentUser(
            onResult = { user ->
                currentUser = user

                // ✅ Sirf pehli baar ya jab friends list change ho
                if (user != null && friendsListener == null) {
                    observeFriends(user.friends)
                }

                if (chatListener == null) {
                    observeChats()
                }
            },
            onError = {
                error = it
                isLoading = false
            }
        )
    }

    private fun observeFriends(friendIds: List<String>) {
        // ✅ Purana listener remove karo
        friendsListener?.remove()

        if (friendIds.isEmpty()) {
            friends = emptyList()
            isLoading = false
            return
        }

        // ✅ Naya listener lagao
        friendsListener = userRepo.listenFriendsByIds(friendIds) {
            friends = it
            isLoading = false
        }
    }

    private fun observeChats() {
        val uid = currentUser?.uid ?: return

        chatListener = chatRepo.listenChatList(uid) {
            chats = it
        }
    }

    override fun onCleared() {
        userListener?.remove()
        friendsListener?.remove()  // ✅ CLEANUP
        chatListener?.remove()
        super.onCleared()
    }
}




