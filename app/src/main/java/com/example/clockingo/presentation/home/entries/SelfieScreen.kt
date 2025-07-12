package com.example.clockingo.presentation.home.entries

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Base64
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.presentation.viewmodel.EntryViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.util.*
import android.view.Surface
import android.widget.Toast
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat

@OptIn(ExperimentalPermissionsApi::class, ExperimentalGetImage::class)
@Composable
fun SelfieScreen(
    entryViewModel: EntryViewModel,
    currentUserId: Int,
    currentLocationId: Int,
    onQrScanned: (Int) -> Unit,
    onClearLocation: () -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(Surface.ROTATION_0)
            .build()
    }

    var cameraReady by remember { mutableStateOf(false) }
    var hasCaptured by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Text("The app needs camera permission to take a selfie.")
        return
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageCapture
            )
            cameraReady = true
        } catch (e: Exception) {
            onError("Front camera failed to start: ${e.message}")
        }
    }

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        Button(
            onClick = {
                if (!hasCaptured && cameraReady) {
                    takeSelfie(
                        context = context,
                        imageCapture = imageCapture,
                        currentUserId = currentUserId,
                        currentLocationId = currentLocationId,
                        entryViewModel = entryViewModel,
                        onQrScanned = {
                            hasCaptured = true
                            onQrScanned(it)
                        },
                        onClearLocation = onClearLocation,
                        onError = onError
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = cameraReady && !hasCaptured
        ) {
            Text("Take Selfie")
        }
    }
}

fun takeSelfie(
    context: Context,
    imageCapture: ImageCapture,
    currentUserId: Int,
    currentLocationId: Int,
    entryViewModel: EntryViewModel,
    onQrScanned: (Int) -> Unit,
    onClearLocation: () -> Unit,
    onError: (String) -> Unit
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val bitmap = imageProxy.toBitmap()?.resizeTo128x128()
                val byteArray = bitmap?.toByteArray()
                val base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())

                val entry = Entry(
                    id = 0,
                    userId = currentUserId,
                    locationId = currentLocationId,
                    entryTime = now,
                    selfie = base64,
                    updatedAt = now,
                    isSynced = true,
                    deviceId = "Device001"
                )

                entryViewModel.createEntry(entry) { success ->
                    if (success) {
                        vibrateSelfie(context)
                        Toast.makeText(context, "Entry created successfully!", Toast.LENGTH_SHORT).show()
                        onClearLocation()
                        onQrScanned(currentLocationId)
                    } else {
                        onError("Error guardando entrada")
                    }
                }

                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                onError("Error capturando selfie: ${exception.message}")
            }
        }
    )
}

fun ImageProxy.toBitmap(): Bitmap? {
    val buffer: ByteBuffer = planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}

fun Bitmap.resizeTo256x256(): Bitmap = Bitmap.createScaledBitmap(this, 256, 256, true)
fun Bitmap.resizeTo128x128(): Bitmap = Bitmap.createScaledBitmap(this, 128, 128, true)
fun Bitmap.resizeTo64x64(): Bitmap = Bitmap.createScaledBitmap(this, 64, 64, true)
fun Bitmap.resizeTo32x32(): Bitmap = Bitmap.createScaledBitmap(this, 32, 32, true)

fun Bitmap.toByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    return stream.toByteArray()
}
fun vibrateSelfie(context: Context, durationMillis: Long = 200) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(durationMillis, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        vibrator.vibrate(durationMillis)
    }
}