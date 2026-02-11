package com.example.ashrut.findx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.ashrut.findx.presentation.navgraph.AppNavGraph
import com.example.ashrut.findx.presentation.navgraph.MainScaffold
import com.example.ashrut.findx.ui.theme.FindXTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            val navController = rememberNavController()
            MainScaffold(navController)
        }
    }
}
