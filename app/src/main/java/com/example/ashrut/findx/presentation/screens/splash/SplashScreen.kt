package com.example.ashrut.findx.presentation.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ashrut.findx.data.route.Routes
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = viewModel()
) {
    LaunchedEffect(true) {
        delay(1200)
        navController.navigate(viewModel.getStartDestination()) {
            popUpTo(Routes.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "SimpleChat 💬",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashPreview() {
    SplashScreen(navController = rememberNavController())
}