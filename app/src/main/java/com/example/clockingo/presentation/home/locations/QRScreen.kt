package com.example.clockingo.presentation.home.locations

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import android.graphics.Bitmap
import android.graphics.Color as AwtColor
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.BackHandler
import com.google.mlkit.vision.barcode.common.Barcode

@Composable
fun QRScreen(locationCode: String, onBack: () -> Unit) {
    val qrBitmap = generateQRCodeFind(locationCode)

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
        } ?: Text("Error generating QR code", color = Color.Red)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}

fun generateQRCodeFind(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) AwtColor.BLACK else AwtColor.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}