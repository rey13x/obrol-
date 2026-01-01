package com.bagas.obrol.ui.screen.chat

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.bagas.obrol.R
import com.bagas.obrol.domain.model.message.AudioMessage
import com.bagas.obrol.domain.model.message.FileMessage
import com.bagas.obrol.domain.model.message.Message
import com.bagas.obrol.domain.model.message.TextMessage
import com.bagas.obrol.ui.ChatTopAppBar
import com.bagas.obrol.ui.components.AudioMessageComponent
import com.bagas.obrol.ui.components.AudioRecordingControls
import com.bagas.obrol.ui.components.FileMessageComponent
import com.bagas.obrol.ui.components.FileMessageInput
import com.bagas.obrol.ui.components.TextMessageComponent
import com.bagas.obrol.ui.components.TextMessageInput
import com.bagas.obrol.ui.navigation.NavigationDestination
import com.bagas.obrol.ui.screen.call.CallDestination
import com.bagas.obrol.ui.screen.info.InfoDestination
import kotlinx.coroutines.flow.collectLatest
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ChatDestination : NavigationDestination {
    override val route = "obrolan"
    override val titleRes = R.string.chat_screen
    const val accountIdArg = "accountId"
    val routeWithArgs = "$route/{$accountIdArg}"
}

@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            chatViewModel.sendCallRequest()
        }
    }

    val currentAccount by chatViewModel.ownAccount.collectAsState(initial = null)
    val chatState by chatViewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        chatViewModel.events.collectLatest { event ->
            when (event) {
                is ChatEvent.NavigateToCall -> {
                    navController.navigate("${CallDestination.route}/${event.accountId}/outgoing")
                }
            }
        }
    }

    LaunchedEffect(chatState.messages) {
        chatViewModel.updateMessagesState(chatState.messages)
    }

    Scaffold(
        topBar = {
            ChatTopAppBar(
                title = chatState.contact?.profile?.username ?: "Obrolan",
                isOpponentOnline = chatState.isOpponentOnline,
                isOpponentTyping = chatState.isOpponentTyping,
                canNavigateBack = true,
                onCallButtonClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        chatViewModel.sendCallRequest()
                    } else {
                        requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onInfoButtonClick = {
                    chatState.contact?.let {
                        navController.navigate("${InfoDestination.route}/${it.account.accountId}")
                    }
                },
                modifier = modifier,
                navigateUp = { navController.navigateUp() },
                contactImageFileName = chatState.contact?.profile?.imageFileName
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
        ) {
            currentAccount?.let { account ->
                MessagesList(
                    messages = chatState.messages,
                    chatViewModel = chatViewModel,
                    accountId = account.accountId,
                    modifier = Modifier.weight(1f)
                )

                SendMessageInput(
                    onStartRecording = {
                        chatViewModel.startRecordingAudio()
                    },
                    onStopRecording = {
                        chatViewModel.stopRecordingAudio()
                    },
                    onCancelRecording = {
                        chatViewModel.stopRecordingAudio()
                    },
                    onSendTextMessage = { messageText ->
                        chatViewModel.sendTextMessage(messageText)
                    },
                    onSendFileMessage = { fileUri ->
                        chatViewModel.sendFileMessage(fileUri)
                    },
                    onSendAudioMessage = {
                        chatViewModel.sendAudioMessage()
                    },
                    onTyping = { isTyping ->
                        chatViewModel.sendTypingStatus(isTyping)
                    }
                )
            }
        }
    }
}

// ... (rest of the file is unchanged)

@Composable
fun MessagesList(
    messages: List<Message>, chatViewModel: ChatViewModel, accountId: Long, modifier: Modifier
) {

    val listState = rememberLazyListState()

    val groupedMessages = remember(messages) { groupMessagesByDate(messages) }

    LaunchedEffect(groupedMessages) {
        if (groupedMessages.isNotEmpty()) {
            listState.animateScrollToItem(groupedMessages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        items(
            items = groupedMessages,
            key = { item ->
                when (item) {
                    is Message -> item.messageId
                    is String -> item
                    else -> item.hashCode()
                }
            }
        ) { item ->
            when (item) {
                is String -> DateSeparator(date = item)
                is Message -> MessageItem(
                    message = item,
                    accountId = accountId,
                    chatViewModel = chatViewModel
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: Message, accountId: Long, chatViewModel: ChatViewModel) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        when (message) {
            is TextMessage -> {
                TextMessageComponent(message, currentAccountId = accountId)
            }

            is FileMessage -> {
                FileMessageComponent(message, currentAccountId = accountId)
            }

            is AudioMessage -> {
                AudioMessageComponent(
                    message,
                    currentAccountId = accountId,
                    startPlayingAudio = { fileName, startPosition ->
                        chatViewModel.startPlayingAudio(
                            File(context.filesDir, fileName),
                            startPosition
                        )
                    },
                    stopPlayingAudio = {
                        chatViewModel.stopPlayingAudio()
                    },
                    getCurrentPlaybackPosition = {
                        chatViewModel.getCurrentPlaybackPosition()
                    },
                    isPlaybackComplete = { chatViewModel.isPlaybackComplete() }
                )
            }
        }
    }
}

@Composable
fun SendMessageInput(
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onCancelRecording: () -> Unit,
    onSendTextMessage: (String) -> Unit,
    onSendFileMessage: (Uri) -> Unit,
    onSendAudioMessage: () -> Unit,
    onTyping: (Boolean) -> Unit
) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var isRecording by remember { mutableStateOf(false) }
    var isTyping by remember { mutableStateOf(false) }

    var attachedFileUri by remember { mutableStateOf<Uri?>(null) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            attachedFileUri = it
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 10.dp)
    ) {

        if (!isTyping && !isRecording) {
            FileMessageInput(
                fileUri = attachedFileUri,
                onSendFile = { uri ->
                    onSendFileMessage(uri)
                    attachedFileUri = null
                },
                onClick = {
                    filePickerLauncher.launch("*/*")
                },
                onDeleteFile = {
                    attachedFileUri = null
                }
            )
        }

        if (attachedFileUri == null && !isRecording) {
            TextMessageInput(
                isTyping = isTyping,
                textFieldValue = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    val newIsTyping = it.text.isNotEmpty()
                    if (newIsTyping != isTyping) {
                        isTyping = newIsTyping
                        onTyping(isTyping)
                    }
                },
                onSendTextMessage = onSendTextMessage,
                modifier = Modifier.weight(1f)
            )
        }

        if ((!isTyping && attachedFileUri == null) || isRecording) {
            AudioRecordingControls(
                isRecording = isRecording,
                onStartRecording = {
                    onStartRecording()
                    isRecording = true
                },
                onStopRecording = {
                    onStopRecording()
                    isRecording = false
                },
                onCancelRecording = {
                    onCancelRecording()
                    isRecording = false
                },
                onSendAudioMessage = {
                    onSendAudioMessage()
                    isRecording = false
                },
                modifier = Modifier
            )
        }
    }
}

@Composable
fun DateSeparator(date: String) {
    Text(
        text = date,
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center
    )
}

fun groupMessagesByDate(messages: List<Message>): List<Any> {
    val groupedMessages = mutableListOf<Any>()
    var lastDate: String? = null

    for (message in messages) {
        val calendar = Calendar.getInstance()
        val today = calendar.get(Calendar.DAY_OF_YEAR)

        calendar.timeInMillis = message.timestamp
        val messageDay = calendar.get(Calendar.DAY_OF_YEAR)
        val messageYear = calendar.get(Calendar.YEAR)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val dateString = when {
            today == messageDay && currentYear == messageYear -> "Hari ini"
            today - 1 == messageDay && currentYear == messageYear -> "Kemarin"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(message.timestamp))
        }

        if (dateString != lastDate) {
            lastDate = dateString
            groupedMessages.add(dateString)
        }
        groupedMessages.add(message)
    }

    return groupedMessages
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    SendMessageInput(
        onStartRecording = {},
        onStopRecording = {},
        onCancelRecording = {},
        onSendTextMessage = {},
        onSendFileMessage = {},
        onSendAudioMessage = {},
        onTyping = {}
    )
}