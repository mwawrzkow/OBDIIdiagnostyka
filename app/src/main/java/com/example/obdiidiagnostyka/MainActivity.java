package com.example.obdiidiagnostyka;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    IntentFilter filter = new IntentFilter();
    private final BroadcastReceiver mReciver = new BroadcastReceiver() {
        private static final String TAG = "Bluetooth TAG";

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG, "Doing:" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String DeviceName = device.getName();
                Log.v(TAG, "Found Device:" + device.getName());
                if (DeviceName != null)
                    if (DeviceName.equals("raspberry")) {
                        try {
                            BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("5dc85d5b-4bdd-40ca-ab9c-c700c088c6c4"));
                            socket.connect();

                            InputStream inputStream = socket.getInputStream();
                            OutputStream outputStream = socket.getOutputStream();
                            String hw = new String("Hello World");

                            outputStream.write(hw.getBytes(Charset.defaultCharset()));

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
        setContentView(R.layout.activity_main);
        Button button = (Button) findViewById(R.id.button);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Connection", "Doing");
//                final BluetoothManager bluetoothManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE).getAdapter();
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (!isGpsEnabled) {
                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 1);
                }
                BluetoothAdapter bluetoothAdapter;
                bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
                if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.enable();
                }
                if (!bluetoothAdapter.isEnabled())
                    return;
                registerReceiver(mReciver, filter);
                Log.v("BluetoothDiscovery", bluetoothAdapter.startDiscovery() ? "Working" : "Not Working");
            }
        });
    }
}