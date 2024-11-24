package ar.edu.utn.frba.homeassistant.ui.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ar.edu.utn.frba.homeassistant.R
import ar.edu.utn.frba.homeassistant.ui.SnackbarManager
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceScreen(
    navController: NavHostController,
    onCreate: (Long, String, String) -> Unit,
) {
    val context = LocalContext.current
    var deviceId by remember { mutableStateOf("") }
    var deviceIdValid by remember { mutableStateOf(true) }
    var deviceName by remember { mutableStateOf("") }
    var deviceType by remember { mutableStateOf("") }

    val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .enableAutoZoom()
        .build()

    val scanner = GmsBarcodeScanning.getClient(context, options)

    fun onScanQrCode() {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val barcodeText = barcode.rawValue
                if (barcodeText != null) {
                    try {
                        val deviceData = JSONObject(barcodeText)
                        val id = deviceData.getLong("id")
                        val name = deviceData.getString("name")
                        val type = deviceData.getString("type")
                        onCreate(id, name, type)
                        navController.popBackStack()
                    } catch (e: Exception) {
                        SnackbarManager.showMessage("Error parsing QR code: ${e.message}")
                    }
                }
            }.addOnFailureListener { e ->
                SnackbarManager.showMessage("Error scanning QR code: ${e.message}")
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_new_device)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = deviceId,
                onValueChange = { value ->
                    deviceId = value
                    deviceIdValid = value.toLongOrNull() != null
                },
                label = { Text(stringResource(R.string.device_id)) },
                modifier = Modifier.fillMaxWidth(),
                isError = deviceIdValid.not(),
                supportingText = {
                    if (deviceIdValid.not()) {
                        Text(stringResource(R.string.device_id_must_be))
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = deviceName,
                onValueChange = { deviceName = it },
                label = { Text(stringResource(R.string.device_name)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = deviceType,
                onValueChange = { deviceType = it },
                label = { Text(stringResource(R.string.device_type)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (deviceId.isNotBlank() && deviceIdValid && deviceName.isNotBlank() && deviceType.isNotBlank()) {
                        onCreate(deviceId.toLong(), deviceName, deviceType)
                        navController.popBackStack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.add_device))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Or", modifier = Modifier.align(androidx.compose.ui.Alignment.CenterHorizontally))

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onScanQrCode() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.scan_QR))
            }
        }
    }
}
