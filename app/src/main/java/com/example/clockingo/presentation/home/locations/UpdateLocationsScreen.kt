package com.example.clockingo.presentation.home.locations

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.clockingo.domain.model.Location
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateLocationsScreen(
    locationViewModel: LocationViewModel,
    location: Location,
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    val loadedLocation by locationViewModel.currentLocation.collectAsState()

    LaunchedEffect(location.id) {
        locationViewModel.getLocationById(location.id)
    }

    if (
        loadedLocation == null ||
        loadedLocation!!.code.isBlank() ||
        loadedLocation!!.createdBy == 0
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    var code by remember { mutableStateOf(loadedLocation!!.code) }
    var address by remember { mutableStateOf(loadedLocation!!.address ?: "") }
    var city by remember { mutableStateOf(loadedLocation!!.city ?: "") }
    var isCompanyOffice by remember { mutableStateOf(loadedLocation!!.isCompanyOffice) }
    val qrBitmap = generateQRCodeUpdate(code)

    BackHandler {
        onFinish()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Update Location",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        qrBitmap?.let { bitmap ->
            AndroidView(factory = { context ->
                ImageView(context).apply {
                    setImageBitmap(bitmap)
                }
            })
        } ?: Text("Error generating QR code", color = Color.Red)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isCompanyOffice,
                onCheckedChange = { isCompanyOffice = it }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Is Company Office?")
        }

        Button(
            onClick = {
                if (code.isNotBlank()) {
                    val updatedLocation = location.copy(
                        code = code,
                        address = if (address.isNotBlank()) address else null,
                        city = if (city.isNotBlank()) city else null,
                        isCompanyOffice = isCompanyOffice
                    )
                    locationViewModel.updateLocation(updatedLocation) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Location updated successfully!", Toast.LENGTH_SHORT).show()
                            onFinish()
                        } else {
                            Toast.makeText(context, "Failed to update location.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill in the 'Code' field.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("Update Location")
        }
    }
}

fun generateQRCodeUpdate(content: String): Bitmap? {
    return try {
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bmp.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}