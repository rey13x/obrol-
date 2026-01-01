package com.bagas.obrol.ui.screen.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bagas.obrol.R
import com.bagas.obrol.domain.model.device.Account
import com.bagas.obrol.domain.model.device.Contact
import com.bagas.obrol.domain.model.device.Profile
import com.bagas.obrol.ui.navigation.NavigationDestination
import java.io.File

object CallDestination : NavigationDestination {
    override val route = "panggilan"
    override val titleRes = R.string.call_screen
    const val accountIdArg = "accountId"
    const val callStateArg = "callState"
    val routeWithArgs = "$route/{$accountIdArg}/{$callStateArg}"
}

@Composable
fun CallScreen(
    callViewModel: CallViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val callResponse by callViewModel.networkManager.callResponse.collectAsState()

    LaunchedEffect(callResponse) {
        callResponse?.let {
            if(it.accepted) {
                callViewModel.startCall()
            } else {
                callViewModel.networkManager.resetCallStateFlows()
                navController.popBackStack()
            }
        }
    }

    val callEnd by callViewModel.networkManager.callEnd.collectAsState()

    LaunchedEffect(callEnd) {
        callEnd?.let {
            callViewModel.endCall()
            navController.popBackStack()
        }
    }

    val callState by callViewModel.uiState.collectAsState()

    CallScreenContent(callState, callViewModel, navController, modifier)
}

@Composable
fun CallScreenContent(
    callState: CallViewState,
    callViewModel: CallViewModel? = null,
    navController: NavHostController? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Menampilkan UI hanya jika kontak tidak null
        callState.contact?.let { contact ->
            if (contact.profile.imageFileName != null) {
                AsyncImage(
                    model = File(LocalContext.current.filesDir, contact.profile.imageFileName),
                    contentDescription = "Gambar Profil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.account_circle_24px),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
                    contentDescription = "Gambar Profil Default",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = contact.profile.username,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        } ?: run {
            // Tampilan placeholder saat kontak masih dimuat
            Text(
                text = if (callState.callState == CallState.SENT_CALL_REQUEST) "Memanggil..." else "Panggilan Masuk...",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.fillMaxHeight(0.4f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            when(callState.callState) {
                CallState.SENT_CALL_REQUEST -> {
                    CallActionButton(
                        iconResId = R.drawable.call_phone_24px, // Menggunakan ikon yang ada sebagai fallback
                        contentDescription = "Tutup",
                        onClick = {
                            callViewModel?.sendCallEnd()
                            navController?.popBackStack()
                        },
                        buttonColor = Color.Red,
                        iconTintColor = Color.White
                    )
                }

                CallState.RECEIVED_CALL_REQUEST -> {
                    CallActionButton(
                        iconResId = R.drawable.call,
                        contentDescription = "Terima Panggilan",
                        onClick = {
                            callViewModel?.acceptCall()
                            callViewModel?.startCall()
                        },
                        buttonColor = Color.Blue, // Warna biru untuk terima panggilan
                        iconTintColor = Color.White
                    )

                    CallActionButton(
                        iconResId = R.drawable.call_phone_24px,
                        contentDescription = "Tolak Panggilan",
                        onClick = {
                            callViewModel?.declineCall()
                            navController?.popBackStack()
                        },
                        buttonColor = Color.Red, // Warna merah untuk tolak
                        iconTintColor = Color.White
                    )
                }

                CallState.CALL -> {
                    CallActionButton(
                        iconResId = R.drawable.volume_up_24px,
                        contentDescription = if (callState.isSpeakerOn) "Speaker Aktif" else "Speaker",
                        onClick = {
                            if (callState.isSpeakerOn) {
                                callViewModel?.setSpeakerOff()
                            } else {
                                callViewModel?.setSpeakerOn()
                            }
                        },
                        buttonColor = if (callState.isSpeakerOn) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        iconTintColor = if (callState.isSpeakerOn) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
                    )

                    CallActionButton(
                        iconResId = R.drawable.call_phone_24px,
                        contentDescription = "Tutup",
                        onClick = {
                            callViewModel?.sendCallEnd()
                            callViewModel?.endCall()
                            navController?.popBackStack()
                        },
                        buttonColor = Color.Red,
                        iconTintColor = Color.White
                    )
                }
                null -> {
                    // Tidak menampilkan apa-apa jika state null
                }
            }
        }
    }
}

@Composable
fun CallActionButton(
    iconResId: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = MaterialTheme.colorScheme.primary,
    iconTintColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(64.dp)
            .background(buttonColor, CircleShape)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = contentDescription,
            tint = iconTintColor
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CallScreenPreview() {
    val contact = Contact(
        account = Account(-1, 0),
        profile = Profile(-1, 0, "Bagas", null)
    )
    val callState = CallViewState(CallState.RECEIVED_CALL_REQUEST, contact, false)
    CallScreenContent(callState)
}
