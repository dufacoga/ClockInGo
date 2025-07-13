package com.example.clockingo.presentation.home.entries

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    locationViewModel: LocationViewModel,
    onQrScanned: (Int) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
    var hasScanned by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    if (!cameraPermissionState.status.isGranted) {
        Text("The app needs camera permission to scan QR codes.")
        return
    }

    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    LaunchedEffect(Unit) {
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val scanner = BarcodeScanning.getClient()

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        analysis.setAnalyzer(
            ContextCompat.getMainExecutor(context)
        ) { imageProxy ->
            if (hasScanned) {
                imageProxy.close()
                return@setAnalyzer
            }

            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(inputImage)
                    .addOnSuccessListener { barcodes ->
                        val rawValue = barcodes.firstOrNull()?.rawValue
                        if (!rawValue.isNullOrEmpty()) {
                            hasScanned = true
                            locationViewModel.getLocationByCode(rawValue)
                        }
                    }
                    .addOnFailureListener { onError("Error scanning the QR code") }
                    .addOnCompleteListener { imageProxy.close() }
            } else {
                imageProxy.close()
            }
        }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, analysis)
        } catch (e: Exception) {
            onError("Error starting camera: ${e.message}")
        }
        Toast.makeText(context, "Please scan the QR code first!", Toast.LENGTH_SHORT).show()
    }
    val foundLocation by locationViewModel.currentLocation.collectAsState()

    LaunchedEffect(foundLocation) {
        if (foundLocation != null) {
            vibrateScan(context)
            Toast.makeText(context, "Now take a selfie!", Toast.LENGTH_SHORT).show()
            onQrScanned(foundLocation!!.id)
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}

fun vibrateScan(context: Context, durationMillis: Long = 200) {
    val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(VibratorManager::class.java)
        vm.defaultVibrator
    } else {
        context.getSystemService(Vibrator::class.java)
    }

    val effect: VibrationEffect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        VibrationEffect.createOneShot(
            durationMillis,
            VibrationEffect.DEFAULT_AMPLITUDE
        )
    } else {
        @Suppress("DEPRECATION")
        return vibrator.vibrate(durationMillis)
    }

    vibrator.vibrate(effect)
}