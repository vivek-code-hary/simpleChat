package com.example.ashrut.findx.presentation.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashrut.findx.data.repository.AuthRepository
import com.example.ashrut.findx.data.route.UiState
import kotlinx.coroutines.launch

class SignupViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    fun signup(
        name: String,
        email: String,
        password: String
    ) {
        // ✅ Name validation
        if (name.isBlank()) {
            uiState = UiState.Error("Name is required")
            return
        }
        if (name.length < 3) {
            uiState = UiState.Error("Name must be at least 3 characters")
            return
        }

        if (name.contains(" ")) {
            uiState = UiState.Error("Username should not contain spaces")
            return
        }

        // ✅ Email validation
        if (email.isBlank()) {
            uiState = UiState.Error("Email is required")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = UiState.Error("Enter a valid email address")
            return
        }

        // ✅ Password validation
        if (password.length < 6) {
            uiState = UiState.Error("Password must be at least 6 characters")
            return
        }
        if (!password.any { it.isDigit() }) {
            uiState = UiState.Error("Password must contain at least 1 number")
            return
        }
        if (!password.any { it.isLetter() }) {
            uiState = UiState.Error("Password must contain at least 1 letter")
            return
        }

        viewModelScope.launch {
            uiState = UiState.Loading

            val result = authRepository.signup(
                name = name.trim(),
                email = email.trim(),
                password = password
            )

            uiState = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Signup failed"
                )
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState = UiState.Loading

            val result = authRepository.loginWithGoogle(idToken)

            uiState = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Google sign-in failed"
                )
            }
        }
    }

}

