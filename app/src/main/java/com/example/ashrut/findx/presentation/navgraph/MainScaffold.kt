package com.example.ashrut.findx.presentation.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ashrut.findx.domain.bottom.bottomNavItems
import com.example.ashrut.findx.presentation.screens.home.HomeBottomBar
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScaffold(
    navController: NavHostController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = bottomNavItems.map { it.route }
    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                HomeBottomBar(navController)
            }
        }
    ) { padding ->

        AppNavGraph(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}
