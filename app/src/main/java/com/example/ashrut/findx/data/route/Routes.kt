package com.example.ashrut.findx.data.route

import android.net.Uri

sealed class Routes(val route: String) {

    object Splash : Routes("splash")

    object Login : Routes("login")

    object Signup : Routes("signup")

    object Home : Routes("home")

    object Users : Routes("users")              // all users screen
    object FriendRequests : Routes("requests")

    object Profile: Routes("profile")
    object Chat : Routes(
        "chat?uid={uid}&name={name}&photo={photo}"
    ) {
        fun passUser(uid: String, name: String, photo: String?): String {
            return "chat?" +
                    "uid=$uid" +
                    "&name=${Uri.encode(name)}" +
                    "&photo=${Uri.encode(photo ?: "")}"
        }
    }
}
