package com.example.ashrut.findx.presentation.screens.users

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.ashrut.findx.data.route.Routes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*

@Composable
fun UsersScreen(
    navController: NavController,
    viewModel: UsersViewModel = viewModel()
) {
    val users = viewModel.filteredUsers
    val me = viewModel.currentUser

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        /* ---------------- SEARCH BAR ---------------- */

        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = viewModel::onSearchChange,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            },
            placeholder = {
                Text("Search users")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(24.dp),
            singleLine = true
        )

        /* ---------------- USERS LIST ---------------- */

        LazyColumn {
            items(users) { user ->

                val isFriend =
                    me?.friends?.contains(user.uid) == true

                val sent =
                    me?.requestsSent?.contains(user.uid) == true

                val received =
                    me?.requestsReceived?.contains(user.uid) == true

                UserRow(
                    name = user.name,
                    isFriend = isFriend,
                    sent = sent,
                    received = received,
                    onAdd = {
                        viewModel.sendRequest(user.uid)
                    },
                    onCancel = {
                        viewModel.cancelRequest(user.uid)
                    },
                    onReceivedClick = {
                        navController.navigate(Routes.FriendRequests.route)
                    }
                )
            }
        }
    }
}
@Composable
private fun UserRow(
    name: String,
    isFriend: Boolean,
    sent: Boolean,
    received: Boolean,
    onAdd: () -> Unit,
    onCancel: () -> Unit,
    onReceivedClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        /* Avatar */
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.first().uppercase(),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = name,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.Medium
        )

        when {
            isFriend -> {
                Text(
                    text = "Friends",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.SemiBold
                )
            }

            sent -> {
                Text(
                    text = "Requested",
                    color = Color.Gray,
                    modifier = Modifier.clickable { onCancel() }
                )
            }

            received -> {
                Text(
                    text = "Accept",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onReceivedClick() }
                )
            }

            else -> {
                Button(
                    onClick = onAdd,
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(
                        horizontal = 14.dp,
                        vertical = 6.dp
                    )
                ) {
                    Text("Add")
                }
            }
        }
    }

    Divider()
}



