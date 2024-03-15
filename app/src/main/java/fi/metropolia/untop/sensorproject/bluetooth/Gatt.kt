package fi.metropolia.untop.sensorproject.bluetooth

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import fi.metropolia.untop.sensorproject.R
import fi.metropolia.untop.sensorproject.data.MyViewModel
import java.util.UUID

class GattClientCallback(private val context: Context, private val viewModel: MyViewModel) :
    BluetoothGattCallback() {
    private fun convertFromInteger(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L
        val value = (i and -0x1).toLong()
        return UUID(MSB or (value shl 32), LSB)
    }

    private val HEART_RATE_SERVICE_UUID = convertFromInteger(0x180D)
    private val HEART_RATE_MEASUREMENT_CHAR_UUID = convertFromInteger(0x2A37)
    val CLIENT_CHARACTERISTIC_CONFIG_UUID = convertFromInteger(0x2902)

    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        Log.d("DBG", "Connection status: $status")
        super.onConnectionStateChange(gatt, status, newState)

        when (newState) {
            BluetoothProfile.STATE_CONNECTING -> {
                Log.d("DBG", "Connecting to GATT service")
                viewModel.connectionState.postValue("Connecting to device...")
            }
            BluetoothProfile.STATE_CONNECTED -> {
                Log.d("DBG", "Connected to GATT service")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("DBG", "No Bluetooth permission")
                    viewModel.connectionState.postValue(context.getString(R.string.gatt_connection_permission))
                    return
                }
                gatt.discoverServices()
            }
            BluetoothProfile.STATE_DISCONNECTED -> {
                Log.d("DBG", "Disconnected from GATT service")
                // Inform the user about the disconnection event.
                viewModel.connectionState.postValue("Disconnected from device")
            }
            BluetoothProfile.STATE_DISCONNECTING -> {
                Log.d("DBG", "Disconnecting from GATT service")
                // You may show a progress dialog or a loading indicator to indicate the disconnection process.
            }
        }
        when (status) {
            BluetoothGatt.GATT_FAILURE -> {
                Log.d("DBG", "GATT connection failure")
                viewModel.connectionState.postValue("Failed to connect to device")
            }
            BluetoothGatt.GATT_SUCCESS -> {
                Log.d("DBG", "GATT connection success")
                viewModel.connectionState.postValue("Connected to device")
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                gatt.device.createBond()
            }
        }
    }


    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        super.onServicesDiscovered(gatt, status)
        if (status != BluetoothGatt.GATT_SUCCESS) {
            Log.d("DBG", "GATT Failed")
            return
        }
        Log.d("DBG", "onServicesDiscovered()")
        for (gattService in gatt.services) {
            Log.d("DBG", "Service ${gattService.uuid}")

            if (gattService.uuid == HEART_RATE_SERVICE_UUID) {
                Log.d("DBG", "BINGO!!!")
                for (gattCharacteristic in gattService.characteristics) {
                    Log.d("DBG", "Characteristic ${gattCharacteristic.uuid}")
                }
                val characteristic = gatt.getService(HEART_RATE_SERVICE_UUID)
                    .getCharacteristic(HEART_RATE_MEASUREMENT_CHAR_UUID)
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                gatt.setCharacteristicNotification(characteristic, true)
                val descriptor = characteristic.getDescriptor(CLIENT_CHARACTERISTIC_CONFIG_UUID)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                gatt.writeDescriptor(descriptor)
            }
        }
    }

    override fun onDescriptorWrite(
        gatt: BluetoothGatt,
        descriptor: BluetoothGattDescriptor,
        status: Int
    ) {
        Log.d("DBG", "onDescriptorWrite")
    }

    @Deprecated("Deprecated in Java")
    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        Log.d("DBG","Pls show stuff ${characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1)}")
        if (characteristic.uuid == HEART_RATE_MEASUREMENT_CHAR_UUID) {
            val rateValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1)
            if (rateValue != null) {
                val rate = rateValue.toInt()
                /*TODO*/
            } else {
                Log.e("DBG", "Heart rate characteristic value is null")
            }
        }
    }


}