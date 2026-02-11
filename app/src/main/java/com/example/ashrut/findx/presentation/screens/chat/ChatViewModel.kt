package com.example.ashrut.findx.presentation.screens.chat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.ashrut.findx.data.repository.ChatRepository
import com.example.ashrut.findx.data.repository.UserRepository
import com.example.ashrut.findx.domain.model.Message
import com.example.ashrut.findx.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration


class ChatViewModel : ViewModel() {

    private val chatRepo = ChatRepository()
    private val auth = FirebaseAuth.getInstance()

    val myId: String? = auth.currentUser?.uid

    var messages = mutableStateOf<List<Message>>(emptyList())
        private set

    // ✅ THIS IS WHAT TOPBAR NEEDS
    private var msgListener: ListenerRegistration? = null

    private var userListener: ListenerRegistration? = null

    fun start(friendId: String) {
        listenMessages(friendId)


        val uid = myId ?: return
        chatRepo.markMessagesSeen(uid,friendId)
        chatRepo.clearUnreadCount(uid,friendId)
    }

    private fun listenMessages(friendId: String) {
        val uid = myId ?: return

        msgListener?.remove()
        msgListener = chatRepo.listenMessages(uid, friendId) {
            messages.value = it
        }
    }



    fun sendMessage(friendId: String, text: String, replyTo: String?) {
        val uid = myId ?: return
        if (text.isBlank()) return

        chatRepo.sendMessage(
            Message(
                senderId = uid,
                receiverId = friendId,
                text = text,
                replyTo = replyTo
            )
        )
    }



    fun deleteMessage(friendId: String, message: Message) {
        val uid = myId ?: return
        val chatId = listOf(uid, friendId).sorted().joinToString("_")
        chatRepo.deleteMessage(chatId, message.id)
    }

    override fun onCleared() {
        msgListener?.remove()
        userListener?.remove()
        super.onCleared()
    }
}



