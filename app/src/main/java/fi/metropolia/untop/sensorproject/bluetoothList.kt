package fi.metropolia.untop.sensorproject

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import fi.metropolia.untop.sensorproject.bluetooth.GattClientCallback
import fi.metropolia.untop.sensorproject.data.MyViewModel

@Composable
fun BluetoothList(
    bluetoothAdapter: BluetoothAdapter?,
    viewModel: MyViewModel,
    permissionsGranted: HashMap<String, Boolean>,
    requestPermissionsLauncher: ActivityResultLauncher<Array<String>>,
    requiredPermissions: Array<String>,
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    val gattClientCallback = GattClientCallback(context = context, viewModel = viewModel)
    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false; viewModel.connectionState.postValue("") },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 5.dp, bottom = 85.dp)
            ) {
                SearchList(
                    viewModel = viewModel,
                    requestPermissionsLauncher = requestPermissionsLauncher,
                    requiredPermissions = requiredPermissions,
                    bluetoothAdapter = bluetoothAdapter,
                    gattClientCallback = gattClientCallback
                )
            }
        }
    }
    Box(Modifier.fillMaxSize()) {
        ElevatedButton(
            onClick = {
                showDialog = true
                if (bluetoothAdapter != null && permissionsGranted.containsKey("android.permission.BLUETOOTH_SCAN")) {
                    viewModel.scanDevices(
                        bluetoothAdapter.bluetoothLeScanner, context
                    )
                } else {
                    AlertDialog.Builder(context).setTitle("Permission Required")
                        .setMessage("This app requires Bluetooth scanning permission to function properly.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }.show()
                }
            },

            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 5.dp)
        ) {
            Text(stringResource(id = R.string.home_scan))
        }
    }
}


@Composable
private fun SearchList(
    viewModel: MyViewModel,
    requestPermissionsLauncher: ActivityResultLauncher<Array<String>>,
    requiredPermissions: Array<String>,
    bluetoothAdapter: BluetoothAdapter?,
    gattClientCallback: GattClientCallback
) {
    val connectionState by viewModel.connectionState.observeAsState("TEST")
    val context = LocalContext.current
    val list by viewModel.scanResults.observeAsState(emptyList())
    if (ActivityCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH_CONNECT
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionsLauncher.launch(requiredPermissions)
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        if (!list.isNullOrEmpty()) {
            Text(text = connectionState, fontWeight = FontWeight.Bold)
            LazyColumn {
                items(list) { item ->
                    val itemName = if (!item.device.name.isNullOrEmpty()) {
                        item.device.name
                    } else ""
                    val connectable =
                        if (item.isConnectable) MaterialTheme.colorScheme.primary else Color.Gray
                    Surface(onClick = {
                        viewModel.connectionState.postValue("")
                        connectToDevice(
                            bluetoothAdapter, item.device.address, context, gattClientCallback
                        )
                    }) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = itemName,
                                color = connectable,
                            )
                            Text(
                                text = "${item.device.address} ${item.rssi}dBm",
                                color = connectable,
                                modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }
                    }
                }
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                Column(
                    Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                    Text(
                        text = stringResource(id = R.string.home_searching)
                    )
                }
            }
        }
    }
}

private fun connectToDevice(
    bluetoothAdapter: BluetoothAdapter?,
    address: String,
    context: Context,
    gattClientCallback: GattClientCallback
) {
    if (bluetoothAdapter == null) {
        Log.e("ConnectToDevice", "BluetoothAdapter is null")
        return
    }

    val bluetoothDevice: BluetoothDevice? = bluetoothAdapter.getRemoteDevice(address)
    if (bluetoothDevice == null) {
        Log.e("ConnectToDevice", "BluetoothDevice is null")
        return
    }

    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.BLUETOOTH
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e("ConnectToDevice", "Bluetooth permission not granted")
        return
    }

    bluetoothDevice.createBond()
    bluetoothDevice.connectGatt(context, false, gattClientCallback)

}
