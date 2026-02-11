package com.example.ashrut.findx.presentation.screens.profile


import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ashrut.findx.data.repository.ProfileRepository
import com.example.ashrut.findx.data.repository.UserRepository
import com.example.ashrut.findx.domain.model.User
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val profileRepo = ProfileRepository()
    private val userRepo = UserRepository()

    var user by mutableStateOf<User?>(null)
        private set

    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var success by mutableStateOf<String?>(null)
        private set

    init {
        userRepo.listenCurrentUser(
            onResult = { user = it },
            onError = { error = it }
        )
    }

    fun removePhoto() {
        viewModelScope.launch {
            loading = true
            error = null

            val result = profileRepo.removeProfilePhoto()

            if (result.isSuccess) {
                success = "Profile photo removed"
            } else {
                error = result.exceptionOrNull()?.message
            }

            loading = false
        }
    }


    /* ---------------- UPDATE NAME ---------------- */

    fun updateName(newName: String) {
        if (newName.length < 3) {
            error = "Name must be at least 3 characters"
            return
        }

        if (newName == user?.name) {
            error = "Name is same as current"
            return
        }

        viewModelScope.launch {
            loading = true
            error = null

            val alreadyUsed =
                userRepo.isNameAlreadyUsed(newName.trim())

            if (alreadyUsed) {
                error = "This name is already taken"
                loading = false
                return@launch
            }

            profileRepo.updateName(newName.trim())
            success = "Name updated"
            loading = false
        }
    }

    /* ---------------- UPDATE PHOTO ---------------- */

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            loading = true
            error = null

            val result = profileRepo.uploadProfileImage(uri)

            if (result.isSuccess) {
                profileRepo.updateProfilePhoto(result.getOrThrow())
                success = "Profile photo updated"
            } else {
                error = result.exceptionOrNull()?.message
            }

            loading = false
        }
    }

    fun logout() {
        profileRepo.logout()
    }
}
