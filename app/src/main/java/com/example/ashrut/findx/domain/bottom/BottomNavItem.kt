package com.example.ashrut.findx.domain.bottom


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ashrut.findx.data.route.Routes

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = Routes.Home.route
    ),
    BottomNavItem(
        title = "Search",
        icon = Icons.Default.Search,
        route = Routes.Users.route
    ),
    BottomNavItem(
        title = "Profile",
        icon = Icons.Default.Person,
        route = Routes.Profile.route
    )
)
