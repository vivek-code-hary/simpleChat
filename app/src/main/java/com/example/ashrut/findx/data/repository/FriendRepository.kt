package com.example.ashrut.findx.data.repository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class FriendRepository {

    private val db = FirebaseFirestore.getInstance()
    private fun users() = db.collection("users")

    fun sendRequest(fromId: String, toId: String) {
        db.runBatch { batch ->
            batch.update(
                users().document(fromId),
                "requestsSent",
                FieldValue.arrayUnion(toId)
            )

            batch.update(
                users().document(toId),
                "requestsReceived",
                FieldValue.arrayUnion(fromId)
            )
        }.addOnSuccessListener {
            println("✅ Request sent successfully")
        }.addOnFailureListener { e ->
            println("❌ Send request failed: ${e.message}")
        }
    }

    fun acceptRequest(myId: String, fromId: String) {
        db.runBatch { batch ->
            // My document update
            batch.update(
                users().document(myId),
                mapOf(
                    "friends" to FieldValue.arrayUnion(fromId),
                    "requestsReceived" to FieldValue.arrayRemove(fromId)
                )
            )

            // Their document update
            batch.update(
                users().document(fromId),
                mapOf(
                    "friends" to FieldValue.arrayUnion(myId),
                    "requestsSent" to FieldValue.arrayRemove(myId)
                )
            )
        }.addOnSuccessListener {
            println("✅ Request accepted")
        }.addOnFailureListener { e ->
            println("❌ Accept failed: ${e.message}")
        }
    }

    fun cancelRequest(fromId: String, toId: String) {
        db.runBatch { batch ->
            batch.update(
                users().document(fromId),
                "requestsSent",
                FieldValue.arrayRemove(toId)
            )

            batch.update(
                users().document(toId),
                "requestsReceived",
                FieldValue.arrayRemove(fromId)
            )
        }.addOnSuccessListener {
            println("✅ Request cancelled")
        }.addOnFailureListener { e ->
            println("❌ Cancel failed: ${e.message}")
        }
    }

    fun rejectRequest(myId: String, fromId: String) {
        db.runBatch { batch ->
            batch.update(
                users().document(myId),
                "requestsReceived",
                FieldValue.arrayRemove(fromId)
            )

            batch.update(
                users().document(fromId),
                "requestsSent",
                FieldValue.arrayRemove(myId)
            )
        }.addOnSuccessListener {
            println("✅ Request rejected")
        }.addOnFailureListener { e ->
            println("❌ Reject failed: ${e.message}")
        }
    }
}