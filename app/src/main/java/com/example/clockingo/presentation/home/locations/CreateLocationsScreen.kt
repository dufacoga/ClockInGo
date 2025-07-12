package com.example.clockingo.presentation.home.locations

import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.clockingo.domain.model.Location
import com.example.clockingo.presentation.viewmodel.LocationViewModel
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateLocationsScreen(
    locationViewModel: LocationViewModel,
    currentUserId: Int
) {
    var code by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isCompanyOffice by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Create New Location",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Address (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (address.isNotBlank() || city.isNotBlank()) {
                    val raw = "${address.trim()} - ${city.trim()}"
                    val codeGenerated1 = raw.toBase64()
                    val codeGenerated2 = codeGenerated1.toBase16()
                    val codeGenerated3 = codeGenerated2.toBase64()

                    val newLocation = Location(
                        id = 0,
                        code = codeGenerated3,
                        address = if (address.isNotBlank()) address else null,
                        city = if (city.isNotBlank()) city else null,
                        createdBy = currentUserId,
                        isCompanyOffice = isCompanyOffice
                    )

                    locationViewModel.createLocation(newLocation) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, "Location created successfully!", Toast.LENGTH_SHORT).show()
                            address = ""
                            city = ""
                            isCompanyOffice = false
                        } else {
                            Toast.makeText(context, "Failed to create location.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, "Please fill at least Address or City.", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text("Create Location")
        }
    }
}

fun String.toBase64(): String =
    Base64.encodeToString(this.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

fun String.toBase16(): String =
    this.toByteArray(Charsets.UTF_8).joinToString("") { "%02x".format(it) }