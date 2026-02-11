package com.example.ashrut.findx.presentation.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ashrut.findx.data.route.Routes
import com.example.ashrut.findx.domain.model.User
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.ashrut.findx.domain.bottom.bottomNavItems
import com.example.ashrut.findx.domain.model.ChatMeta
import com.example.ashrut.findx.presentation.screens.users.UserAvatar
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextAlign


@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val friends = viewModel.friends
    val chats = viewModel.chats
    val isLoading = viewModel.isLoading
    val error = viewModel.error
    val myId = viewModel.myId

    Scaffold(
        topBar = {
            HomeTopBar(
                navController = navController,
                requestCount = viewModel.currentUser?.requestsReceived?.size ?: 0
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(error ?: "Something went wrong")
                }
            }

            friends.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No friends yet 😔\nSearch users and add friends",
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {

                val sortedFriends = friends.sortedByDescending { user ->
                    chats.find { it.userIds.contains(user.uid) }
                        ?.lastTimestamp ?: 0L
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(sortedFriends) { user ->

                        val chat: ChatMeta? =
                            chats.find { it.userIds.contains(user.uid) }

                        val unread =
                            chat?.unreadCount?.get(myId) ?: 0

                        ChatListItem(
                            user = user,
                            lastMessage = chat?.lastMessage ?: "",
                            lastMessageSenderId = chat?.lastMessageSenderId,
                            lastMessageDelivered = chat?.lastMessageDelivered ?: false,
                            lastMessageSeen = chat?.lastMessageSeen ?: false,
                            myId = myId,
                            time = chat?.lastTimestamp ?: 0L,
                            unreadCount = unread
                        ) {
                            navController.navigate(
                                Routes.Chat.passUser(
                                    uid = user.uid,
                                    name = user.name,
                                    photo = user.photoUrl
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}




/* -------------------- CHAT ROW -------------------- */

@Composable
fun ChatListItem(
    user: User,
    lastMessage: String,
    lastMessageSenderId: String?,
    lastMessageDelivered: Boolean,
    lastMessageSeen: Boolean,
    myId: String?,
    time: Long,
    unreadCount: Int,
    onClick: () -> Unit
) {

    val isSentByMe = lastMessageSenderId == myId

    val tickIcon = when {
        lastMessageSeen -> Icons.Default.DoneAll
        lastMessageDelivered -> Icons.Default.DoneAll
        else -> Icons.Default.Done
    }

    val tickColor =
        if (lastMessageSeen) Color(0xFF34B7F1) else Color.Gray

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        UserAvatar(
            name = user.name,
            photoUrl = user.photoUrl
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {

            Text(
                user.name,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSentByMe) {
                    Icon(
                        tickIcon,
                        null,
                        modifier = Modifier.size(16.dp),
                        tint = tickColor
                    )
                    Spacer(Modifier.width(4.dp))
                }

                Text(
                    lastMessage,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight =
                        if (unreadCount > 0) FontWeight.Bold
                        else FontWeight.Normal
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(formatHomeTime(time), fontSize = 12.sp)

            if (unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        unreadCount.toString(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}




/* -------------------- TIME FORMAT -------------------- */

fun formatHomeTime(timestamp: Long): String {
    if (timestamp == 0L) return ""

    val date = Date(timestamp)
    val today = Calendar.getInstance()
    val cal = Calendar.getInstance().apply { time = date }

    return if (
        today.get(Calendar.DAY_OF_YEAR) ==
        cal.get(Calendar.DAY_OF_YEAR)
    ) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date)
    } else {
        SimpleDateFormat("dd/MM", Locale.getDefault()).format(date)
    }
}



/* -------------------- TOP BAR -------------------- */

@Composable
fun HomeTopBar(
    navController: NavController,
    requestCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            "SimpleChat 💬",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                navController.navigate(Routes.FriendRequests.route)
            }
        ) {
            BadgedBox(
                badge = {
                    if (requestCount > 0) {
                        Badge { Text(requestCount.toString()) }
                    }
                }
            ) {
                Icon(
                    Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}





@Composable
fun HomeBottomBar(
    navController: NavController
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { item ->

            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(Routes.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(item.title)
                }
            )
        }
    }
}






