package com.example.ashrut.findx.presentation.navgraph

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.ashrut.findx.data.route.Routes
import com.example.ashrut.findx.presentation.screens.chat.ChatScreen
import com.example.ashrut.findx.presentation.screens.friendrequest.FriendRequestScreen
import com.example.ashrut.findx.presentation.screens.home.HomeScreen
import com.example.ashrut.findx.presentation.screens.login.LoginScreen
import com.example.ashrut.findx.presentation.screens.profile.ProfileScreen
import com.example.ashrut.findx.presentation.screens.signup.SignupScreen
import com.example.ashrut.findx.presentation.screens.splash.SplashScreen
import com.example.ashrut.findx.presentation.screens.users.UsersScreen
import androidx.compose.ui.Modifier

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route,
        modifier = modifier
    ) {

        composable(Routes.Splash.route) {
            SplashScreen(navController)
        }

        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        composable(Routes.Signup.route) {
            SignupScreen(navController)
        }

        /* -------- MAIN SCREENS (BottomBar visible) -------- */

        composable(Routes.Home.route) {
            HomeScreen(navController)
        }

        composable(Routes.Users.route) {
            UsersScreen(navController)
        }

        composable(Routes.Profile.route) {
            ProfileScreen(navController)
        }

        composable(Routes.FriendRequests.route) {
            FriendRequestScreen(navController)
        }

        /* -------- CHAT (NO BottomBar) -------- */

        composable(
            route = Routes.Chat.route,
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
                navArgument("photo") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->

            val uid = backStackEntry.arguments?.getString("uid")!!
            val name = backStackEntry.arguments?.getString("name")!!
            val photo = backStackEntry.arguments?.getString("photo")

            ChatScreen(
                friendId = uid,
                friendName = name,
                friendPhoto = photo,
                navController = navController
            )
        }

    }
}


