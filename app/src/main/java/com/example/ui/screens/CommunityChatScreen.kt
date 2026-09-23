package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SocietyChatMessageEntity
import com.example.ui.ArihantViewModel
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityChatScreen(
    viewModel: ArihantViewModel,
    modifier: Modifier = Modifier
) {
    val allMessages by viewModel.allChatMessages.collectAsState()
    val currentUserName by viewModel.currentUserName.collectAsState()
    val currentFlatId by viewModel.currentFlatId.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedChannel by viewModel.selectedChatChannel.collectAsState()

    var messageInput by remember { mutableStateOf("") }
    val channels = listOf("General Society", "Tower Kaveh", "Buy & Sell / Help", "Events & Sports")
    val channelDisplayNames = listOf("📢 General Society", "🏢 Tower Kaveh", "🛒 Buy/Sell & Help", "🎉 Events & Sports")

    val selectedChannelIndex = channels.indexOf(selectedChannel).let { if (it >= 0) it else 0 }

    val filteredMessages = remember(allMessages, selectedChannel) {
        allMessages.filter { it.channel.equals(selectedChannel, ignoreCase = true) }
    }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = OffWhiteBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header with Candy styling
            Surface(
                color = PureWhiteSurface,
                shadowElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    CandySectionHeading(
                        title = "Society Community Chat",
                        subtitle = "Active channel: $selectedChannel • Arihant Alishan",
                        icon = Icons.Default.Forum,
                        flavor = LightCandyFlavor.PASTEL_SKY,
                        badgeText = "${filteredMessages.size} Messages"
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    CandyPillTabRow(
                        tabs = channelDisplayNames,
                        selectedIndex = selectedChannelIndex,
                        onTabSelected = { idx ->
                            viewModel.selectChatChannel(channels[idx])
                        }
                    )
                }
            }

            // Chat Messages List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (filteredMessages.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CandyIconBadge(
                            icon = Icons.Default.ChatBubbleOutline,
                            flavor = LightCandyFlavor.PASTEL_LAVENDER,
                            size = 56.dp,
                            iconSize = 30.dp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "No messages yet in $selectedChannel",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Be the first neighbor to start the conversation!",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredMessages, key = { it.id }) { msg ->
                            val isMe = msg.senderFlatId == currentFlatId || msg.senderName == currentUserName
                            ChatMessageItem(
                                message = msg,
                                isMe = isMe
                            )
                        }
                    }
                }
            }

            // Quick topic chips
            Surface(
                color = PureWhiteSurface,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val quickPhrases = listOf(
                            "Good morning neighbors! ☕",
                            "Is water supply normal? 💧",
                            "Lifts running smoothly 👍",
                            "Need electrician contact ⚡",
                            "Anyone playing badminton today? 🏸"
                        )
                        items(quickPhrases) { phrase ->
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFFF1F5F9),
                                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                modifier = Modifier.clickable {
                                    messageInput = phrase
                                }
                            ) {
                                Text(
                                    text = phrase,
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Input bar with Light Candy Styling
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = messageInput,
                            onValueChange = { messageInput = it },
                            placeholder = {
                                Text(
                                    "Chat as $currentUserName ($currentFlatId)...",
                                    fontSize = 12.sp,
                                    color = TextMuted
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_chat_message"),
                            shape = RoundedCornerShape(20.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = PureWhiteSurface,
                                unfocusedContainerColor = PureWhiteSurface,
                                focusedBorderColor = Color(0xFF38BDF8),
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        CandyButton(
                            text = "Send",
                            onClick = {
                                if (messageInput.isNotBlank()) {
                                    viewModel.sendChatMessage(messageInput, selectedChannel)
                                    messageInput = ""
                                    coroutineScope.launch {
                                        if (filteredMessages.isNotEmpty()) {
                                            listState.animateScrollToItem(filteredMessages.size)
                                        }
                                    }
                                }
                            },
                            icon = Icons.Default.Send,
                            flavor = CandyFlavor.SAPPHIRE,
                            enabled = messageInput.isNotBlank(),
                            shape = RoundedCornerShape(20.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                            modifier = Modifier.testTag("btn_send_chat")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: SocietyChatMessageEntity,
    isMe: Boolean,
    modifier: Modifier = Modifier
) {
    val messageFlavor = when {
        message.isAnnouncement -> LightCandyFlavor.PASTEL_ROSE
        isMe -> LightCandyFlavor.PASTEL_SKY
        else -> LightCandyFlavor.PASTEL_MINT
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isMe) {
            // Sender Avatar
            Surface(
                shape = CircleShape,
                color = Color.Transparent,
                modifier = Modifier
                    .size(34.dp)
                    .shadow(
                        elevation = 2.dp,
                        shape = CircleShape,
                        ambientColor = messageFlavor.shadowTint.copy(alpha = 0.3f),
                        spotColor = messageFlavor.shadowTint.copy(alpha = 0.4f)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(messageFlavor.topGradient, messageFlavor.bottomGradient)
                            ),
                            shape = CircleShape
                        )
                        .border(
                            BorderStroke(1.dp, messageFlavor.borderStroke),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = message.senderName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = messageFlavor.contentColor
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Message Bubble
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            color = Color.Transparent,
            modifier = Modifier
                .widthIn(max = 280.dp)
                .shadow(
                    elevation = 3.dp,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isMe) 16.dp else 4.dp,
                        bottomEnd = if (isMe) 4.dp else 16.dp
                    ),
                    ambientColor = messageFlavor.shadowTint.copy(alpha = 0.25f),
                    spotColor = messageFlavor.shadowTint.copy(alpha = 0.35f)
                )
        ) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            if (isMe) {
                                listOf(Color(0xFFF0F9FF), Color(0xFFE0F2FE))
                            } else if (message.isAnnouncement) {
                                listOf(Color(0xFFFFF1F2), Color(0xFFFCE7F3))
                            } else {
                                listOf(PureWhiteSurface, Color(0xFFF8FAFC))
                            }
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            if (isMe) Color(0xFFBAE6FD) else if (message.isAnnouncement) Color(0xFFFDA4AF) else Color(0xFFE2E8F0)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMe) 16.dp else 4.dp,
                            bottomEnd = if (isMe) 4.dp else 16.dp
                        )
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                // Top gloss reflection
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.White.copy(alpha = 0.5f),
                                0.45f to Color.White.copy(alpha = 0.08f),
                                0.5f to Color.Transparent
                            )
                        )
                )

                Column {
                    if (!isMe) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = message.senderName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = messageFlavor.topGradient,
                                border = BorderStroke(0.8.dp, messageFlavor.borderStroke)
                            ) {
                                Text(
                                    text = message.senderFlatId,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = messageFlavor.contentColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        if (message.isAnnouncement) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "📢 Official Society Notice",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE11D48)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Text(
                        text = message.message,
                        fontSize = 13.sp,
                        color = TextPrimary,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message.timestamp,
                            fontSize = 9.5.sp,
                            color = TextMuted
                        )
                        if (isMe) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.DoneAll,
                                contentDescription = "Sent",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
