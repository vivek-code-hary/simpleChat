package com.example.ashrut.findx.presentation.screens.profile

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ashrut.findx.data.route.Routes
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val user = viewModel.user
    val loading = viewModel.loading
    val error = viewModel.error

    val imagePicker =
        rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            uri?.let { viewModel.uploadPhoto(it) }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                /* ---------------- PROFILE PHOTO ---------------- */

                Box {

                    if (user?.photoUrl.isNullOrEmpty()) {
                        // 🔤 Avatar fallback
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name
                                    ?.firstOrNull()
                                    ?.uppercase()
                                    ?: "?",
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    } else {
                        AsyncImage(
                            model = user?.photoUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                        )
                    }

                    // 📷 change photo
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable {
                                imagePicker.launch("image/*")
                            }
                            .padding(8.dp),
                        tint = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                /* ---------------- NAME & EMAIL ---------------- */

                Text(
                    text = user?.name ?: "",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = user?.email ?: "",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                /* ---------------- REMOVE PHOTO ---------------- */

                if (!user?.photoUrl.isNullOrEmpty()) {
                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Remove profile photo",
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            viewModel.removePhoto()
                        }
                    )
                }

                Spacer(Modifier.height(32.dp))

                /* ---------------- OPTIONS ---------------- */

                ProfileOption(
                    icon = Icons.Default.Settings,
                    title = "Settings"
                )

                ProfileOption(
                    icon = Icons.Default.Logout,
                    title = "Logout",
                    color = Color.Red
                ) {
                    viewModel.logout()
                    navController.navigate(Routes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            /* ---------------- LOADING ---------------- */

            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            /* ---------------- ERROR ---------------- */

            error?.let {
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                ) {
                    Text(it)
                }
            }
        }
    }
}

@Composable
fun ProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick?.invoke() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = color)
    }
}


