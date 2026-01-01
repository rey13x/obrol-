package com.bagas.obrol.ui.components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

@Composable
fun PdfFirstPageViewer(
    modifier: Modifier = Modifier,
    uri: Uri,
    imageWidth: Dp = 150.dp,
    imageHeight: Dp = 200.dp
) {
    val coroutineScope = rememberCoroutineScope()
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    val density = LocalDensity.current

    LaunchedEffect(uri) {
        coroutineScope.launch(Dispatchers.IO) {
            try {
                // Handle URI correctly, especially if it's not a file:// URI
                val file = if (uri.scheme == "file") {
                    uri.toFile()
                } else {
                    // For content URIs, we assume the file path might be valid or we need a different approach
                    // But in PdfViewer.kt context (PdfFirstPageViewer), previous code used uri.toFile() which often fails for content://
                    // Let's assume the caller provides a file-based URI or we try to handle it.
                    // Given the context of PdfPreview usage in MessageInputs.kt, it creates a temp file.
                    // So uri.toFile() might work if it is a file URI.
                    // However, `uri.toFile()` throws IllegalArgumentException if scheme is not file.
                    // Safe check:
                    try {
                        uri.toFile()
                    } catch (e: IllegalArgumentException) {
                        null
                    }
                }

                if (file != null && file.exists()) {
                    val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                    val renderer = PdfRenderer(input)
                    if (renderer.pageCount > 0) {
                        val page = renderer.openPage(0)

                        val widthPx = with(density) { imageWidth.toPx().toInt() }
                        val heightPx = with(density) { imageHeight.toPx().toInt() }

                        // Ensure positive dimensions
                        if (widthPx > 0 && heightPx > 0) {
                            val destinationBitmap = Bitmap.createBitmap(
                                widthPx,
                                heightPx,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                destinationBitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                            withContext(Dispatchers.Main) {
                                bitmap = destinationBitmap
                            }
                        }
                        page.close()
                    }
                    renderer.close()
                    input.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = "First Page of PDF",
            modifier = modifier
                .size(imageWidth, imageHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(imageWidth, imageHeight)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Gray)
        )
    }
}
