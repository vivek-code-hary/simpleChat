package com.example.ashrut.findx.data.repository


import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class ProfileRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private fun uid() =
        auth.currentUser?.uid ?: throw Exception("User not logged in")

    /* ---------------- PHOTO UPLOAD ---------------- */

    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val ref = storage.reference
                .child("profile_images/${uid()}.jpg")

            ref.putFile(uri).await()
            val url = ref.downloadUrl.await()

            Result.success(url.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProfilePhoto(url: String) {
        db.collection("users")
            .document(uid())
            .update("photoUrl", url)
            .await()
    }

    suspend fun removeProfilePhoto(): Result<Unit> {
        return try {
            val userId = uid()

            // 🔥 delete from storage
            val storageRef =
                FirebaseStorage.getInstance()
                    .reference
                    .child("profile_images/$userId.jpg")

            storageRef.delete().await()

            // 🔥 remove from firestore
            db.collection("users")
                .document(userId)
                .update("photoUrl", "")
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    /* ---------------- NAME UPDATE ---------------- */

    suspend fun updateName(name: String) {
        db.collection("users")
            .document(uid())
            .update("name", name)
            .await()
    }

    fun logout() {
        auth.signOut()
    }
}
