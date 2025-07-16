package com.example.clockingo.presentation.home.locations

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
import com.example.clockingo.presentation.utils.QRCodeGenerator
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@Composable
fun QRScreen(
    locationCode: String,
    onBack: () -> Unit
) {
    val qrBitmap = QRCodeGenerator.generateQRCode(locationCode)

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
        qrBitmap?.let { bitmap ->
            AndroidView(factory = { context ->
                ImageView(context).apply {
                    setImageBitmap(bitmap)
                }
            })
        } ?: Text(stringResource(R.string.qr_screen_qr_error), color = Color.Red)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text(stringResource(R.string.common_back))
        }
    }
}