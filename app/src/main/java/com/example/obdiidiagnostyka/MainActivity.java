package com.example.obdiidiagnostyka;

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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    IntentFilter filter = new IntentFilter();
    BluetoothSocket OBDSocket;
    InputStream OBDInput;
    OutputStream OBDOutput;
    DataInputStream InStream;
    BluetoothDevice device;
    private final BroadcastReceiver mReciver = new BroadcastReceiver() {
        private static final String TAG = "Bluetooth TAG";

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG, "Doing:" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {

                device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String DeviceName = device.getName();
                Log.v(TAG, "Found Device:" + device.getName());
                if (DeviceName != null)
                    if (DeviceName.equals("raspberry")) {
                        try {
                            BluetoothAdapter bluetoothAdapter;
                            bluetoothAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
                            bluetoothAdapter.cancelDiscovery();
                            OBDSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("5dc85d5b-4bdd-40ca-ab9c-c700c088c6c4"));
                            OBDSocket.connect();

                            OBDInput = OBDSocket.getInputStream();
                            OBDOutput = OBDSocket.getOutputStream();
                            new bluetoothDeviceHandler(device, OBDSocket, OBDInput, OBDOutput);
                            String hw = new String("EnX88hsumpCFAqBxr#ckpL!7X7G+eDCEyLGq!Bc?X-^s5$BJm*PGfD!tnBtj7f8B@5!XL=Bu#?8p$sAeWUMK=2+5HAXBe9=VhTwE");

                            bluetoothDeviceHandler.getInstance().sendData(hw);
//                             InStream =
//                            byte[] buffer = new byte[1024];  // buffer store for the stream
//                            int bytes; // bytes returned from read()
//                            bytes = InStream.read(buffer) ;
////                            BufferedInputStream bis = new BufferedInputStream(OBDInput);
////                            ByteArrayOutputStream buf = new ByteArrayOutputStream();
////                            int result = bis.read();
////                            while(result != -1)
////                            {
////                                buf.write((byte)result);
////                                result = bis.read();
////                            }
                            String readMessage = bluetoothDeviceHandler.getInstance().readData();
                            if (readMessage.equals("Accept")) {
                                hw = "Connect";
                                bluetoothDeviceHandler.getInstance().sendData(hw);
                                InStream = new DataInputStream(OBDInput);
                                String response = bluetoothDeviceHandler.getInstance().readData();
                                Log.e("Bluetooth", response);
                                startActivity(new Intent(MainActivity.this, erro.class));
//                                intent.putExtra("Socket", OBDSocket);


                            }
                            Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_SHORT).show();


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