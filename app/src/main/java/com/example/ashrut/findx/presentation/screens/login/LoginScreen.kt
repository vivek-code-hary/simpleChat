package com.example.ashrut.findx.presentation.screens.login


import android.app.Activity
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ashrut.findx.R
import com.example.ashrut.findx.data.route.Routes
import com.example.ashrut.findx.data.route.UiState
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException

/* ---------------- GOOGLE CLIENT ---------------- */



/* ---------------- GOOGLE CLIENT ---------------- */

private fun provideGoogleClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(
        GoogleSignInOptions.DEFAULT_SIGN_IN
    )
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()

    return GoogleSignIn.getClient(context, gso)
}

/* ---------------- LOGIN SCREEN ---------------- */

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }  // ✅ ADD

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val uiState = viewModel.uiState
    val isLoading = uiState is UiState.Loading

    /* ---------------- GOOGLE SETUP ---------------- */
    val googleClient = remember { provideGoogleClient(context) }

    val googleLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let { viewModel.loginWithGoogle(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /* ---------------- SHOW ERROR AS SNACKBAR ---------------- */
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                navController.navigate(Routes.Home.route) {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is UiState.Error -> {
                snackbarHostState.showSnackbar(
                    message = uiState.message ?: "Login failed",
                    duration = SnackbarDuration.Short
                )
            }
            else -> {}
        }
    }

    /* ---------------- UI ---------------- */
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }  // ✅ ADD
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(24.dp))

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

                Button(
                    onClick = { viewModel.login(email, password) },
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
                        Text("Login")
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text("OR", color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(16.dp))

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
                    text = "New user? Sign up",
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.Signup.route)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

