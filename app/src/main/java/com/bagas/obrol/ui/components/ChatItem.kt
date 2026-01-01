package com.bagas.obrol.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.bagas.obrol.R
import com.bagas.obrol.domain.model.chat.ChatPreview
import com.bagas.obrol.domain.model.message.FileMessage
import com.bagas.obrol.domain.model.message.TextMessage
import java.io.File

@Composable
fun ChatItem(
    chatPreview: ChatPreview,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (chatPreview.contact.isServiceContact) {
            Image(
                painter = painterResource(id = R.drawable.obrol),
                contentDescription = "Avatar Bot",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            )
        } else if (chatPreview.contact.profile.imageFileName != null) {
            AsyncImage(
                model = File(LocalContext.current.filesDir, chatPreview.contact.profile.imageFileName!!),
                contentDescription = "Foto profil ${chatPreview.contact.profile.username}",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.account_circle_24px),
                contentDescription = "Foto profil default",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = chatPreview.contact.profile.username, fontWeight = FontWeight.Bold)
                if (chatPreview.contact.isServiceContact) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.verified),
                        contentDescription = "Terverifikasi",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            val lastMessageText = when (val lastMessage = chatPreview.lastMessage) {
                is TextMessage -> lastMessage.text
                is FileMessage -> lastMessage.fileName
                else -> "Belum ada pesan"
            }
            Text(
                text = lastMessageText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp
            )
        }
    }
}
