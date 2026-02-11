package com.example.ashrut.findx.presentation.screens.friendrequest

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRequestScreen(
    navController: NavController,
    viewModel: FriendRequestViewModel = viewModel()
) {
    val requests = viewModel.receivedRequests

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Friend Requests",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        /* ---------------- EMPTY STATE ---------------- */

        if (requests.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "No friend requests",
                        fontSize = MaterialTheme.typography.titleMedium.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "When someone sends you a request, it will appear here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        /* ---------------- REQUEST LIST ---------------- */

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            items(requests, key = { it.uid }) { user ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        /* Avatar */
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user.name.first().uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = user.name,
                            modifier = Modifier.weight(1f),
                            fontWeight = FontWeight.Medium
                        )

                        /* Accept */
                        Button(
                            onClick = {
                                viewModel.accept(user.uid)
                            },
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                horizontal = 14.dp,
                                vertical = 6.dp
                            )
                        ) {
                            Text("Accept")
                        }

                        Spacer(Modifier.width(8.dp))

                        /* Reject */
                        OutlinedButton(
                            onClick = {
                                viewModel.reject(user.uid)
                            },
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(
                                horizontal = 12.dp,
                                vertical = 6.dp
                            )
                        ) {
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}


