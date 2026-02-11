package com.example.ashrut.findx.data.repository

import androidx.compose.animation.core.snap
import com.example.ashrut.findx.domain.model.ChatMeta
import com.example.ashrut.findx.domain.model.Message
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions


class ChatRepository {

    private val db = FirebaseFirestore.getInstance()
    private fun chats() = db.collection("chats")

    private fun chatId(a: String, b: String) =
        listOf(a, b).sorted().joinToString("_")

    fun sendMessage(message: Message) {
        val id = chatId(message.senderId, message.receiverId)
        val chatRef = chats().document(id)

        db.runBatch { batch ->
            val msgRef = chatRef.collection("messages").document()
            batch.set(msgRef, message)

            batch.set(
                chatRef,
                mapOf(
                    "chatId" to id,
                    "userIds" to listOf(message.senderId, message.receiverId),
                    "lastMessage" to message.text,
                    "lastMessageSenderId" to message.senderId,
                    "lastTimestamp" to System.currentTimeMillis(),
                    "unreadCount.${message.receiverId}" to FieldValue.increment(1),
                    "unreadCount.${message.senderId}" to 0
                ),
                SetOptions.merge()
            )
        }.addOnSuccessListener {
            println("✅ Message sent successfully")
        }.addOnFailureListener { e ->
            println("❌ ERROR: ${e.message}")  // 👈 YE ERROR DIKHAI DEGA
        }
    }
    fun clearUnreadCount(myId: String, friendId: String) {
        val id = chatId(myId, friendId)
        chats().document(id)
            .update("unreadCount.$myId", 0)
    }

    fun listenMessages(
        myId: String,
        friendId: String,
        onResult: (List<Message>) -> Unit
    ): ListenerRegistration {

        val id = chatId(myId, friendId)
        val chatRef = chats().document(id)

        return chatRef
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snap, _ ->

                val msgs = snap?.documents?.map {
                    it.toObject(Message::class.java)!!.copy(id = it.id)
                } ?: emptyList()

                // ✅ DELIVERED UPDATE (FIXED)
                snap?.documents?.forEach { doc ->
                    val msg = doc.toObject(Message::class.java) ?: return@forEach

                    if (msg.receiverId == myId && !msg.isDelivered) {
                        doc.reference.update("isDelivered", true)

                        chatRef.update(
                            "lastMessageDelivered", true
                        )
                    }
                }

                onResult(msgs)
            }
    }

    fun listenChatList(
        myId: String,
        onResult: (List<ChatMeta>) -> Unit
    ): ListenerRegistration {
        return chats()
            .whereArrayContains("userIds", myId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents
                    ?.mapNotNull { it.toObject(ChatMeta::class.java) }
                    ?: emptyList()

                onResult(list.sortedByDescending { it.lastTimestamp })
            }
    }
    fun deleteMessage(chatId: String, messageId: String) {
        chats()
            .document(chatId)
            .collection("messages")
            .document(messageId)
            .delete()
    }


    fun markMessagesSeen(myId: String, friendId: String) {
        val id = chatId(myId, friendId)
        val chatRef = chats().document(id)

        chatRef
            .collection("messages")
            .whereEqualTo("receiverId", myId)
            .whereEqualTo("isSeen", false)
            .get()
            .addOnSuccessListener { snap ->
                snap.documents.forEach {
                    it.reference.update("isSeen", true)
                }

                // ✅ UPDATE CHAT META
                chatRef.update(
                    "lastMessageSeen", true
                )
            }
    }
}
