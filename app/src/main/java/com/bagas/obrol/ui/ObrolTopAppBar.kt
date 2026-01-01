package com.bagas.obrol.ui

import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bagas.obrol.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObrolTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    onSettingsButtonClick: () -> Unit,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp // Memperbesar ukuran teks Obrol+
                )
            )
        },
        actions = {
            IconButton(
                onClick = onSettingsButtonClick,
                modifier = Modifier.size(48.dp) // Memperbesar area klik ikon pengaturan
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.more),
                    contentDescription = "Pengaturan",
                    modifier = Modifier.size(32.dp) // Memperbesar ukuran ikon pengaturan
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = navigateUp,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Kembali",
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    )
}
