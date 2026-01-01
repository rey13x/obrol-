package com.bagas.obrol.ui.screen.servicechat

import android.content.Intent
import android.net.Uri
import android.os.Looper
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.FileProvider
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.bagas.obrol.R
import com.bagas.obrol.ui.navigation.NavigationDestination
import java.io.File

object ServiceChatDestination : NavigationDestination {
    override val route = "kontak_servis"
    override val titleRes = R.string.service_contact_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceChatScreen(
    navController: NavController,
    viewModel: ServiceChatViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    if (uiState.isQuestionSheetVisible) {
        ModalBottomSheet(
            onDismissRequest = { viewModel.setQuestionSheetVisibility(false) },
            sheetState = sheetState
        ) {
            QuestionTemplatesBubble(
                title = "Pilih pertanyaan di bawah ini:",
                questions = viewModel.allQuestions,
                onQuestionClick = viewModel::onQuestionSelected
            )
        }
    }

    LaunchedEffect(uiState.conversation.size) {
        if (uiState.conversation.isNotEmpty()) {
            listState.animateScrollToItem(uiState.conversation.size - 1)
        }
    }

    Scaffold(
        topBar = {
            ServiceChatTopAppBar(
                onNavigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            MenuButton(onClick = { viewModel.setQuestionSheetVisibility(true) })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {            items(uiState.conversation) { message ->
                when (message) {
                    is ServiceChatMessage.UserMessage -> UserMessageBubble(text = message.text)
                    is ServiceChatMessage.BotMessage -> BotMessageBubble(text = message.text, isFirst = message.isFirstMessage)
                    is ServiceChatMessage.BotImageMessage -> BotImageBubble(imageResId = message.imageResId, caption = message.caption)
                    is ServiceChatMessage.BotVideoMessage -> BotVideoBubble(videoResId = message.videoResId)
                    is ServiceChatMessage.BotFileMessage -> BotFileBubble(fileName = message.fileName, fileResId = message.fileResId)
                    is ServiceChatMessage.ActionButtons -> {
                        ActionButtonsBubble(actions = message.actions) {
                            when (it) {
                                "Instagram" -> {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/13bagas.exv"))
                                    context.startActivity(intent)
                                }
                                "WhatsApp" -> {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281319865384?text=Halo%20saya%20dari%20Obrol%2B%20ingin%20melaporkan%20kendala%20Aplikasi"))
                                    context.startActivity(intent)
                                }
                            }
                        }
                    }
                    is ServiceChatMessage.QuestionTemplates -> { }
                }
            }
        }
    }
}

@Composable
fun UserMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(12.dp)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun BotMessageBubble(text: String, isFirst: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (isFirst) {
            Image(
                painter = painterResource(id = R.drawable.obrol),
                contentDescription = "Avatar Bot",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Spacer(modifier = Modifier.width(48.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomEnd = 16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(12.dp)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun BotImageBubble(@RawRes imageResId: Int, caption: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = caption,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Fit
            )
            if (caption != null) {
                Text(
                    text = caption,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun BotVideoBubble(@RawRes videoResId: Int) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/$videoResId")
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = true
                    }
                }
            )
        }
    }
}

@Composable
fun BotFileBubble(fileName: String, @RawRes fileResId: Int) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Spacer(modifier = Modifier.width(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    try {
                        val inputStream = context.resources.openRawResource(fileResId)
                        val file = File(context.cacheDir, fileName)
                        file.outputStream().use { inputStream.copyTo(it) }

                        val fileUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.provider",
                            file
                        )

                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(fileUri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_file),
                contentDescription = "Ikon Berkas",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = fileName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuestionTemplatesBubble(
    title: String,
    questions: List<String>,
    onQuestionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = title, fontSize = 14.sp, color = Color.Gray)
        questions.forEach { question ->
            Button(
                onClick = { onQuestionClick(question) },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(24.dp)),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text(
                    text = question,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun MenuButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(painter = painterResource(id = R.drawable.ic_menu), contentDescription = "Menu")
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
fun ActionButtonsBubble(actions: List<String>, onActionClick: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        actions.forEach { action ->
            Button(onClick = { onActionClick(action) }) {
                Text(text = action)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceChatTopAppBar(onNavigateUp: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.obrol),
                    contentDescription = "Avatar Bot",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy((-4).dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Obrol+",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.verified),
                            contentDescription = "Terverifikasi",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = "Online",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateUp) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Kembali"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
