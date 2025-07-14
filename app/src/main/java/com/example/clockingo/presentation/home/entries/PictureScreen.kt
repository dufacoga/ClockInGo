package com.example.clockingo.presentation.home.entries

import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import android.widget.ImageView
import androidx.activity.compose.BackHandler
import com.example.clockingo.presentation.utils.decodeBase64ToByteArray
import com.example.clockingo.presentation.utils.decodeBase64ToString
import com.example.clockingo.presentation.utils.decodeByteArrayToBitmap

@Composable
fun PictureScreen(
    selfieBase64: String,
    onBack: () -> Unit
) {
    val stringByteArray: String = selfieBase64.decodeBase64ToString()
    val selfieByteArray: ByteArray = stringByteArray.decodeBase64ToByteArray()
    val selfieBitmap: Bitmap = selfieByteArray.decodeByteArrayToBitmap()
    val scaledBitmap = Bitmap.createScaledBitmap(selfieBitmap, 768, 768, true)

    BackHandler {
        onBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (selfieBitmap != null) {
            AndroidView(factory = { context ->
                ImageView(context).apply {
                    setImageBitmap(scaledBitmap)
                }
            })
        } else {
            Text("Error displaying image", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}