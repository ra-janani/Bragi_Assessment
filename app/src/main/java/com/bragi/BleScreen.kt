package com.bragi

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bragi.blemiddleware.domain.model.Peripheral
import com.bragi.core.PermissionHandler
import com.bragi.core.hasRequiredPermissions

@Composable
fun BleScreen(viewModel: BleViewModel = hiltViewModel()) {

    val discoveryState = viewModel.discoveryState.collectAsState().value
    val context = LocalContext.current
    val connectionState by viewModel.connectionState.collectAsState()
    var showPermissionRequest by remember { mutableStateOf(!hasRequiredPermissions(context)) }

    LaunchedEffect(showPermissionRequest) {
        if (hasRequiredPermissions(context)) {
            viewModel.startScanning()
        }
    }

    if (showPermissionRequest) {
        PermissionHandler(
            onPermissionsGranted = {
                showPermissionRequest = false
            },
            onPermissionsDenied = {
                Toast.makeText(
                    context,
                    "Permissions are required to scan for devices.",
                    Toast.LENGTH_LONG
                ).show()

            }
        )
    } else {

        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Discovered Devices", style = MaterialTheme.typography.bodyLarge)
            Text("Connection Status: $connectionState", style = MaterialTheme.typography.bodyMedium)

            when (discoveryState) {
                is DiscoveryUiState.Idle -> {
                    Text(
                        "Press the button to start scanning",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is DiscoveryUiState.Loading -> {
                    Text("Scanning for devices...", style = MaterialTheme.typography.bodyMedium)
                }

                is DiscoveryUiState.Success -> {
                    DeviceList(devices = discoveryState.devices) { device ->
                        viewModel.connectTo(device)
                    }
                }

                is DiscoveryUiState.Error -> {
                    Text(
                        "Error: ${discoveryState.message}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DeviceList(devices: List<Peripheral>, connectTo: (Peripheral) -> Unit) {
    LazyColumn {
        items(devices) { device ->
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { connectTo(device) }
                        .padding(12.dp)
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(device.name ?: "Unknown", fontWeight = FontWeight.Bold)
                        Text(device.address, style = MaterialTheme.typography.bodyMedium)
                    }
                    Icon(Icons.Default.Check, contentDescription = null)
                }
            }
        }
    }
}