package com.example.ashrut.findx.presentation.screens.signup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ashrut.findx.data.route.Routes
import com.example.ashrut.findx.data.route.UiState
import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.example.ashrut.findx.R


/* ------------------------------------------------ */
/* 🔥 GOOGLE SIGN-IN CLIENT                          */
/* ------------------------------------------------ */

private fun provideGoogleClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN
    )
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

/* ------------------------------------------------ */
/* 🔥 SIGNUP SCREEN                                 */
/* ------------------------------------------------ */

@Composable
fun SignupScreen(
    navController: NavController,
    viewModel: SignupViewModel = viewModel()
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    val isLoading = uiState is UiState.Loading

    /* -------------------- Google launcher -------------------- */

    val googleClient = remember {
        provideGoogleClient(context)
    }

    val googleLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task =
                    GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account =
                        task.getResult(ApiException::class.java)
                    account.idToken?.let {
                        viewModel.signInWithGoogle(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    /* -------------------- Navigation -------------------- */

    LaunchedEffect(uiState) {
        if (uiState is UiState.Success) {
            navController.navigate(Routes.Home.route) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    /* -------------------- UI -------------------- */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Create Account",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                enabled = !isLoading,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            /* ---------------- EMAIL SIGNUP BUTTON ---------------- */

            Button(
                onClick = {
                    viewModel.signup(
                        name = name,
                        email = email,
                        password = password
                    )
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Create Account")
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("OR", color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(16.dp))

            /* ---------------- GOOGLE SIGNUP BUTTON ---------------- */

            OutlinedButton(
                onClick = {
                    googleLauncher.launch(googleClient.signInIntent)
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text("Continue with Google")
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Already have an account? Login",
                modifier = Modifier.clickable {
                    navController.popBackStack()
                },
                color = MaterialTheme.colorScheme.primary
            )
        }

        /* -------------------- ERROR -------------------- */

        if (uiState is UiState.Error) {
            Text(
                text = uiState.message ?: "Something went wrong",
                color = Color.Red,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}


