package com.example.clockingo.presentation.home.entries

import android.content.Context
import android.util.Base64
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clockingo.domain.model.Entry
import com.example.clockingo.presentation.utils.VibrateDevice
import com.example.clockingo.presentation.viewmodel.EntryViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.util.*
import android.view.Surface
import android.widget.Toast
import androidx.compose.ui.unit.dp
import com.example.clockingo.presentation.utils.resizeTo
import com.example.clockingo.presentation.utils.toByteArray
import com.example.clockingo.presentation.utils.toBitmap2
import java.text.SimpleDateFormat
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SelfieScreen(
    entryViewModel: EntryViewModel,
    currentUserId: Int,
    currentLocationId: Int,
    onQrScanned: (Int) -> Unit,
    onClearLocation: () -> Unit,
    onError: (String) -> Unit,
    isOnline: Boolean
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
        Text(stringResource(R.string.selfie_camera_permission_needed))
        return
    }

    LaunchedEffect(Unit) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).get()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
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
            onError(context.getString(R.string.selfie_camera_start_error, e.message ?: R.string.selfie_unknown))
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
                        onError = onError,
                        isOnline = isOnline
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = cameraReady && !hasCaptured
        ) {
            Text(stringResource(R.string.selfie_button_text))
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
    onError: (String) -> Unit,
    isOnline: Boolean
) {
    imageCapture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val bitmap = imageProxy.toBitmap2()
                val resized = bitmap.resizeTo(512)
                val byteArray = resized.toByteArray(12)
                var base64: String? = null
//                    Code tested for MySQL
//                    base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                if (isOnline) {
                    val firstBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                    base64 = Base64.encodeToString(firstBase64.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                } else {
                    val firstBase64 = Base64.encodeToString(byteArray, Base64.NO_WRAP)
                    base64 = Base64.encodeToString(firstBase64.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
                }
//                    Code tested for MySQL
//                    val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(Date())
                val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

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
                        VibrateDevice.vibrate(context)
                        Toast.makeText(context, context.getString(R.string.selfie_entry_success), Toast.LENGTH_SHORT).show()
                        onClearLocation()
                        onQrScanned(currentLocationId)
                    } else {
                        onError(context.getString(R.string.selfie_entry_save_error))
                    }
                }

                imageProxy.close()
            }

            override fun onError(exception: ImageCaptureException) {
                onError(context.getString(R.string.selfie_capture_error, exception.message ?: R.string.selfie_unknown))
            }
        }
    )
}