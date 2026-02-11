package com.example.ashrut.findx.data.repository

import com.example.ashrut.findx.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private fun auth() = FirebaseAuth.getInstance()
    private fun db() = FirebaseFirestore.getInstance()

    fun isUserLoggedIn(): Boolean {
        return auth().currentUser != null
    }

    suspend fun isUsernameTaken(name: String): Boolean {
        val snap = db()
            .collection("users")
            .whereEqualTo("name", name)
            .limit(1)
            .get()
            .await()

        return !snap.isEmpty
    }



    suspend fun login(
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            println("🔥 Login attempt: $email")

            auth().signInWithEmailAndPassword(email, password).await()

            println("✅ Login successful!")
            Result.success(Unit)

        } catch (e: Exception) {
            println("❌ Login error: ${e.message}")

            val message = when {
                e.message?.contains("password is invalid") == true ->
                    "Incorrect password"

                e.message?.contains("no user record") == true ->
                    "No account found with this email"

                e.message?.contains("badly formatted") == true ->
                    "Invalid email format"

                e.message?.contains("disabled") == true ->
                    "This account has been disabled"

                else -> e.message ?: "Login failed"
            }

            Result.failure(Exception(message))
        }
    }


    suspend fun loginWithGoogle(
        idToken: String
    ): Result<Unit> {
        return try {
            val credential =
                GoogleAuthProvider.getCredential(idToken, null)

            val result =
                auth().signInWithCredential(credential).await()

            val user = result.user
                ?: return Result.failure(Exception("User not found"))

            // ✅ Save user only first time
            val doc = db().collection("users")
                .document(user.uid)
                .get()
                .await()

            if (!doc.exists()) {
                val newUser = User(
                    uid = user.uid,
                    name = user.displayName ?: "User",
                    email = user.email ?: ""
                )

                db().collection("users")
                    .document(user.uid)
                    .set(newUser)
                    .await()
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun signup(
        name: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {

            if (isUsernameTaken(name)){
                return Result.failure(
                    Exception("Username already taken")
                )
            }

            val result =
                auth().createUserWithEmailAndPassword(email, password).await()

            val uid = result.user?.uid
                ?: return Result.failure(Exception("UID not found"))

            val user = User(
                uid = uid,
                name = name,
                email = email
            )

            db().collection("users")
                .document(uid)
                .set(user)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth().signOut()
    }
}
