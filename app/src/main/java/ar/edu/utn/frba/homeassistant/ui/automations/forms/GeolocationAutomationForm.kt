package ar.edu.utn.frba.homeassistant.ui.automations.forms

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import ar.edu.utn.frba.homeassistant.GetCurrentCoordinates
import ar.edu.utn.frba.homeassistant.utils.requestLocationPermissions
import com.google.android.gms.location.FusedLocationProviderClient

fun getCurrentCoordinates(context: Context, locationClient: FusedLocationProviderClient, onSuccess: (Double, Double) -> Unit) {
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return
    }
    locationClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            onSuccess(location.latitude, location.longitude)
        }
    }

}

@Composable
fun GeolocationAutomationForm(
    getCurrentCoordinates: GetCurrentCoordinates
) {
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
        ) {
            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    getCurrentCoordinates { lat, long ->
                        latitude = lat.toString()
                        longitude = long.toString()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Use Current Location")
            }
        }

        Column(
        ) {
            Button(
                onClick = { /* Save logic */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Save")
            }
        }
    }


}