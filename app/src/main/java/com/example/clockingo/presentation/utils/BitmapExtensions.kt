package com.example.clockingo.presentation.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import androidx.camera.core.ImageProxy
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream

fun ImageProxy.toBitmap2(): Bitmap {
    val buffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    val original = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    return original.rotate(imageInfo.rotationDegrees)
}

fun Bitmap.rotate(degrees: Int): Bitmap {
    val matrix = Matrix().apply {
        postRotate(degrees.toFloat())
    }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Bitmap.resizeTo(size: Int): Bitmap {
    require(size in listOf(512, 256, 128, 64, 32)) {
        "Only sizes 512, 256, 128, 64, and 32 are supported"
    }
    return this.scale(size, size, filter = true)
}

fun Bitmap.toByteArray(quality: Int): ByteArray {
    require(quality in 0..100) {
        "Quality must be between 0 and 100"
    }
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}

fun String.decodeBase64ToString(): String {
    val decodedBytes = Base64.decode(this, Base64.DEFAULT)
    return String(decodedBytes, Charsets.UTF_8)
}

fun String.decodeBase64ToByteArray(): ByteArray {
    return Base64.decode(this, Base64.DEFAULT)
}

fun ByteArray.decodeByteArrayToBitmap(): Bitmap {
    return BitmapFactory.decodeByteArray(this, 0, this.size)
}