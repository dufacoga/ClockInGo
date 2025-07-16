package com.example.clockingo.presentation.home.locations

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
import com.example.clockingo.presentation.utils.toBase16
import com.example.clockingo.presentation.utils.toBase64
import androidx.compose.ui.res.stringResource
import com.example.clockingo.R

@Composable
fun CreateLocationsScreen(
    locationViewModel: LocationViewModel,
    currentUserId: Int
) {
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
            text = stringResource(R.string.create_location_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(stringResource(R.string.create_location_label_address)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text(stringResource(R.string.create_location_label_city)) },
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
            Text(stringResource(R.string.create_location_checkbox_company_office))
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
                        address = address.ifBlank { null },
                        city = city.ifBlank { null },
                        createdBy = currentUserId,
                        isCompanyOffice = isCompanyOffice
                    )

                    locationViewModel.createLocation(newLocation) { isSuccess ->
                        if (isSuccess) {
                            Toast.makeText(context, context.getString(R.string.create_location_success), Toast.LENGTH_SHORT).show()
                            address = ""
                            city = ""
                            isCompanyOffice = false
                        } else {
                            Toast.makeText(context, context.getString(R.string.create_location_failed), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.create_location_fill_required), Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(12.dp)
        ) {
            Text(stringResource(R.string.create_location_button))
        }
    }
}