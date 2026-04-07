package com.example.whatsapp_sim.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp

fun parseAssetImagePaths(imagePathSpec: String?): List<String> =
    imagePathSpec
        ?.split('|')
        ?.map { it.trim() }
        ?.filter { it.isNotEmpty() }
        .orEmpty()

@Composable
fun AssetImage(
    imagePath: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(imagePath) {
        imagePath?.let {
            try {
                context.assets.open(it).use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFFCCCCCC)),
            contentAlignment = Alignment.Center
        ) {
            Text("📷", fontSize = 40.sp)
        }
    }
}
