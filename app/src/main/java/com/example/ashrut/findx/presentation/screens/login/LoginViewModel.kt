package com.example.ashrut.findx.presentation.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashrut.findx.data.repository.AuthRepository
import com.example.ashrut.findx.data.route.UiState
import kotlinx.coroutines.launch
import android.util.Patterns


class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    var uiState by mutableStateOf<UiState>(UiState.Idle)
        private set

    /* ---------------- EMAIL / PASSWORD LOGIN ---------------- */

    fun login(
        email: String,
        password: String
    ) {
        // ✅ Email validation
        if (email.isBlank()) {
            uiState = UiState.Error("Email is required")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = UiState.Error("Enter a valid email")
            return
        }

        // ✅ Password validation
        if (password.isBlank()) {
            uiState = UiState.Error("Password is required")
            return
        }

        if (password.length < 6) {
            uiState = UiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            uiState = UiState.Loading

            val result = authRepository.login(
                email.trim(),
                password
            )

            uiState = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(
                    result.exceptionOrNull()?.message ?: "Login failed"
                )
            }
        }
    }

    /* ---------------- GOOGLE LOGIN ---------------- */

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            uiState = UiState.Loading

            val result =
                authRepository.loginWithGoogle(idToken)

            uiState = if (result.isSuccess) {
                UiState.Success
            } else {
                UiState.Error(
                    result.exceptionOrNull()?.message
                        ?: "Google login failed"
                )
            }
        }
    }
}

