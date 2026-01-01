package com.bagas.obrol.ui.screen.info

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bagas.obrol.R
import com.bagas.obrol.ui.ObrolTopAppBar
import com.bagas.obrol.ui.components.shareFile
import com.bagas.obrol.ui.navigation.NavigationDestination
import com.bagas.obrol.ui.screen.call.CallDestination
import com.bagas.obrol.ui.screen.call.CallState
import kotlinx.coroutines.launch
import java.io.File

object InfoDestination : NavigationDestination {
    override val route = "info"
    override val titleRes = R.string.info_screen
    const val ACCOUNT_ID_ARG = "accountId"
    val routeWithArgs = "$route/{$ACCOUNT_ID_ARG}"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InfoScreen(
    infoViewModel: InfoViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val callRequest by infoViewModel.networkManager.callRequest.collectAsState()

    LaunchedEffect(callRequest) {
        callRequest?.let {
            navController.navigate("${CallDestination.route}/${it.senderId}/${CallState.RECEIVED_CALL_REQUEST.name}")
        }
    }

    val infoState by infoViewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()
    val animatedSize by animateDpAsState(targetValue = if (pagerState.currentPage == 0) 150.dp else 80.dp)
    val animatedOffset by animateDpAsState(targetValue = if (pagerState.currentPage == 0) 0.dp else (-30).dp)

    Scaffold(
        topBar = {
            ObrolTopAppBar(
                title = "",
                canNavigateBack = true,
                onSettingsButtonClick = {},
                navigateUp = { navController.navigateUp() }
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.offset(y = animatedOffset)) {
                if (infoState.profile.imageFileName != null) {
                    AsyncImage(
                        model = File(LocalContext.current.filesDir, infoState.profile.imageFileName!!),
                        contentDescription = "Foto Profil",
                        modifier = Modifier
                            .size(animatedSize)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.account_circle_24px),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                        contentDescription = "Foto Profil Default",
                        modifier = Modifier
                            .size(animatedSize)
                            .clip(CircleShape)
                    )
                }
            }
            Text(
                text = infoState.profile.username,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    text = { Text("Foto") }
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    text = { Text("Tautan") }
                )
                Tab(
                    selected = pagerState.currentPage == 2,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(2) } },
                    text = { Text("Berkas") }
                )
            }

            HorizontalPager(state = pagerState) { page ->
                when (page) {
                    0 -> LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
                        items(infoState.imageMessages) { message ->
                            val context = LocalContext.current
                            AsyncImage(
                                model = File(context.filesDir, message.fileName),
                                contentDescription = "Gambar",
                                modifier = Modifier
                                    .size(128.dp)
                                    .padding(4.dp)
                                    .clickable { shareFile(context, message.fileName) },
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    1 -> LazyColumn {
                        items(infoState.linkMessages) { message ->
                            Text(text = message.text, modifier = Modifier.padding(16.dp))
                        }
                    }
                    2 -> LazyColumn {
                        items(infoState.fileMessages) { message ->
                            Text(text = message.fileName, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}
