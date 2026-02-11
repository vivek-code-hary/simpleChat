package com.example.ashrut.findx.presentation.screens.splash

import androidx.lifecycle.ViewModel
import com.example.ashrut.findx.data.repository.AuthRepository
import com.example.ashrut.findx.data.route.Routes

class SplashViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    fun getStartDestination(): String {
        return if (authRepository.isUserLoggedIn()) {
            Routes.Home.route
        } else {
            Routes.Login.route
        }
    }
}
