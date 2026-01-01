package com.bagas.obrol.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.bagas.obrol.R
import com.bagas.obrol.domain.model.chat.ChatPreview
import com.bagas.obrol.ui.ObrolTopAppBar
import com.bagas.obrol.ui.components.ChatItem
import com.bagas.obrol.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route = "beranda"
    override val titleRes = R.string.app_name
}

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onChatClick: (ChatPreview) -> Unit,
    onSettingsButtonClick: () -> Unit
) {
    val uiState by homeViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            ObrolTopAppBar(
                title = stringResource(id = R.string.app_name),
                canNavigateBack = false,
                onSettingsButtonClick = onSettingsButtonClick,
                navigateUp = { }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            LazyColumn {
                items(uiState.chatPreviews) { chat ->
                    ChatItem(chatPreview = chat, onClick = { onChatClick(chat) })
                }
            }
        }
    }
}
