package com.example.ashrut.findx.data.repository

import com.example.ashrut.findx.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.tasks.await

class UserRepository {

    private fun auth() = FirebaseAuth.getInstance()
    private fun db() = FirebaseFirestore.getInstance()
    private fun users() = db().collection("users")

    private fun currentUid(): String? = auth().currentUser?.uid

    suspend fun isNameAlreadyUsed(name: String): Boolean {
        val snap = users()
            .whereEqualTo("name", name)
            .get()
            .await()

        return !snap.isEmpty
    }

    // 🔹 Listen current user
    fun listenCurrentUser(
        onResult: (User?) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration? {
        val uid = currentUid() ?: return null

        return users()
            .document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "User error")
                    return@addSnapshotListener
                }
                onResult(snapshot?.toObject(User::class.java))
            }
    }

    // 🔹 Listen all OTHER users (friends = anyone you can chat with)
    fun listenFriendsByIds(
        ids: List<String>,
        onResult: (List<User>) -> Unit
    ): ListenerRegistration? {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return null
        }

        return users()
            .whereIn("uid", ids)
            .addSnapshotListener { snap, _ ->
                val users = snap?.toObjects(User::class.java) ?: emptyList()
                onResult(users)
            }
    }

    // ✅ NEW: Real-time listener for multiple users
    fun listenUsersByIds(
        ids: List<String>,
        onResult: (List<User>) -> Unit
    ): ListenerRegistration? {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return null
        }

        // Firestore whereIn max 10 items allow karta hai
        // Agar 10+ users hain to batches mein split karo
        if (ids.size > 10) {
            // Simple approach: sirf pehle 10 listen karo
            // Production mein proper batching implement karo
            val limitedIds = ids.take(10)

            return users()
                .whereIn("uid", limitedIds)
                .addSnapshotListener { snap, _ ->
                    val users = snap?.toObjects(User::class.java) ?: emptyList()
                    onResult(users)
                }
        }

        return users()
            .whereIn("uid", ids)
            .addSnapshotListener { snap, _ ->
                val users = snap?.toObjects(User::class.java) ?: emptyList()
                onResult(users)
            }
    }

    fun getUsersByIds(
        ids: List<String>,
        onResult: (List<User>) -> Unit
    ) {
        if (ids.isEmpty()) {
            onResult(emptyList())
            return
        }

        users()
            .whereIn("uid", ids)
            .get()
            .addOnSuccessListener { snap ->
                val users = snap.toObjects(User::class.java)
                onResult(users)
            }
    }

    fun listenAllUsers(
        onResult: (List<User>) -> Unit,
        onError: (String) -> Unit
    ): ListenerRegistration {
        return users()
            .addSnapshotListener { snap, err ->
                if (err != null) {
                    onError(err.message ?: "Error")
                    return@addSnapshotListener
                }
                onResult(snap?.toObjects(User::class.java) ?: emptyList())
            }
    }



}
