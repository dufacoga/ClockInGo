package com.example.clockingo.presentation.home.entries

import android.annotation.SuppressLint
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
import com.example.clockingo.presentation.utils.VibrateDevice
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

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
        Text(stringResource(R.string.scan_camera_permission_needed))
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
                    .addOnFailureListener { onError(context.getString(R.string.scan_qr_error)) }
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
            onError(context.getString(R.string.scan_camera_start_error, e.message ?: R.string.scan_unknown))
        }
        Toast.makeText(context, context.getString(R.string.scan_prompt_scan_qr), Toast.LENGTH_SHORT).show()
    }
    val foundLocation by locationViewModel.currentLocation.collectAsState()

    LaunchedEffect(foundLocation) {
        if (foundLocation != null) {
            VibrateDevice.vibrate(context)
            Toast.makeText(context, context.getString(R.string.scan_prompt_selfie), Toast.LENGTH_SHORT).show()
            onQrScanned(foundLocation!!.id)
        }
    }

    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
}