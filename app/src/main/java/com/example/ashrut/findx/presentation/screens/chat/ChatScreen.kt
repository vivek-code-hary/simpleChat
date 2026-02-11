package com.example.ashrut.findx.presentation.screens.chat

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.InsertEmoticon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.ashrut.findx.domain.model.Message
import com.example.ashrut.findx.presentation.screens.users.UserAvatar
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


/* -------------------- CHAT SCREEN -------------------- */

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    friendId: String,
    friendName: String,
    friendPhoto: String?,
    navController: NavController,
    viewModel: ChatViewModel = viewModel()
) {
    var input by remember { mutableStateOf("") }
    var replyMsg by remember { mutableStateOf<Message?>(null) }
    var deleteMsg by remember { mutableStateOf<Message?>(null) }

    val listState = rememberLazyListState()
    val messages = viewModel.messages.value
    val myId = viewModel.myId

    LaunchedEffect(friendId) {
        viewModel.start(friendId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Scaffold(
        topBar = {
            ChatTopBar(
                name = friendName,
                photoUrl = friendPhoto,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            Column {
                replyMsg?.let {
                    ReplyPreviewBar(
                        message = it,
                        onCancel = { replyMsg = null }
                    )
                }

                ChatInputBar(
                    modifier = Modifier,
                    messageText = input,
                    onMessageChange = { input = it },
                    onSendClick = {
                        viewModel.sendMessage(friendId, input, replyMsg?.text)
                        input = ""
                        replyMsg = null
                    }
                )
            }
        }
    ) { padding ->

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            itemsIndexed(messages, key = { _, m -> m.id }) { index, msg ->

                // ✅ DATE HEADER LOGIC
                val showDateHeader =
                    if (index == 0) true
                    else {
                        val prev = messages[index - 1]
                        formatChatDate(prev.timestamp) !=
                                formatChatDate(msg.timestamp)
                    }

                if (showDateHeader) {
                    DateHeader(formatChatDate(msg.timestamp))
                }

                MessageBubble(
                    message = msg,
                    isSender = msg.senderId == myId,
                    onReply = { replyMsg = it },
                    onDelete = { deleteMsg = it }
                )
            }
        }

        deleteMsg?.let {
            AlertDialog(
                onDismissRequest = { deleteMsg = null },
                title = { Text("Delete message?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteMessage(friendId, it)
                        deleteMsg = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { deleteMsg = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 12.dp, vertical = 4.dp)
        )
    }
}

/* -------------------- TOP BAR -------------------- */

@Composable
fun ChatTopBar(
    name: String,
    photoUrl: String?,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = onBackClick) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        UserAvatar(
            name = name,
            photoUrl = photoUrl,   // ✅ NULL allowed
            size = 40.dp
        )


        Spacer(Modifier.width(12.dp))

        Column {
            Text(
                text = name,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )

        }
    }
}


/* -------------------- INPUT BAR -------------------- */

@Composable
fun ChatInputBar(
    modifier: Modifier,
    messageText: String,
    onMessageChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    val isTyping = messageText.isNotBlank()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically   // ✅ FIX
    ) {

        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically // ✅ FIX
            ) {

                Icon(
                    imageVector = Icons.Outlined.InsertEmoticon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 36.dp, max = 120.dp),
                    contentAlignment = Alignment.CenterStart // ✅ TEXT STABLE
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = onMessageChange,
                        textStyle = LocalTextStyle.current.copy(
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (messageText.isEmpty()) {
                                Text(
                                    text = "Message",
                                    color = MaterialTheme.colorScheme
                                        .onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                            inner()
                        }
                    )
                }

                Spacer(Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.6f)
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        FloatingActionButton(
            onClick = onSendClick,
            modifier = Modifier.size(48.dp), // 👈 WhatsApp size
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = if (isTyping) Icons.Default.Send else Icons.Default.Mic,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}






/* -------------------- MESSAGE BUBBLE -------------------- */


@Composable
fun MessageBubble(
    message: Message,
    isSender: Boolean,
    onReply: (Message) -> Unit,
    onDelete: (Message) -> Unit
) {
    val bubbleColor =
        if (isSender) MaterialTheme.colorScheme.primaryContainer
        else MaterialTheme.colorScheme.surfaceVariant

    val textColor =
        if (isSender) MaterialTheme.colorScheme.onPrimaryContainer
        else MaterialTheme.colorScheme.onSurfaceVariant

    var offsetX by remember { mutableStateOf(0f) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start
    ) {
        Box {

            if (offsetX > 16f) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                )
            }

            Column(
                modifier = Modifier
                    .offset { IntOffset(offsetX.toInt(), 0) }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { _, drag ->
                                if (drag > 0) {
                                    offsetX = (offsetX + drag).coerceAtMost(120f)
                                }
                            },
                            onDragEnd = {
                                if (offsetX > 80f) onReply(message)
                                offsetX = 0f
                            }
                        )
                    }
                    .combinedClickable(
                        onClick = {},
                        onLongClick = { onDelete(message) }
                    )
                    .background(
                        bubbleColor,
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isSender) 16.dp else 4.dp,
                            bottomEnd = if (isSender) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(max = 280.dp)
            ) {

                message.replyTo?.let {
                    ReplyPreviewMini(it, isSender)
                    Spacer(Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    fontSize = 15.sp,
                    color = textColor,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formatTime(message.timestamp),
                        fontSize = 11.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )

                    if (isSender) {
                        Spacer(Modifier.width(4.dp))
                        MessageStatusIcon(message)
                    }
                }
            }
        }
    }
}

/* -------------------- MESSAGE STATUS -------------------- */

@Composable
fun MessageStatusIcon(message: Message) {
    val icon = when {
        message.isSeen -> Icons.Default.DoneAll
        message.isDelivered -> Icons.Default.DoneAll
        else -> Icons.Default.Done
    }

    val color = when {
        message.isSeen -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = color,
        modifier = Modifier.size(16.dp)
    )
}

/* -------------------- HELPERS -------------------- */

@Composable
fun ReplyPreviewBar(
    message: Message,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .width(4.dp)
                .height(32.dp)
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Replying to",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = message.text,
                fontSize = 13.sp,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(onClick = onCancel) {
            Icon(Icons.Default.Close, contentDescription = null)
        }
    }
}

@Composable
fun ReplyPreviewMini(
    text: String,
    isSender: Boolean
) {
    Row(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f),
                RoundedCornerShape(6.dp)
            )
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )

        Spacer(Modifier.width(6.dp))

        Column {
            Text(
                text = if (isSender) "You" else "Reply",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = text,
                fontSize = 12.sp,
                maxLines = 1
            )
        }
    }
}

fun formatTime(timestamp: Timestamp): String =
    SimpleDateFormat("hh:mm a", Locale.getDefault())
        .format(timestamp.toDate())

@RequiresApi(Build.VERSION_CODES.O)
fun formatChatDate(timestamp: Timestamp): String {
    val date = timestamp.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

    val today = LocalDate.now()

    return when {
        date == today -> "Today"
        date == today.minusDays(1) -> "Yesterday"
        else -> date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
    }
}






























