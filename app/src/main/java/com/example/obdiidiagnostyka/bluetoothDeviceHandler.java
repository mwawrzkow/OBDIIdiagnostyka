package com.example.obdiidiagnostyka;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class bluetoothDeviceHandler {
    private static bluetoothDeviceHandler bluetoothDeviceHandler;

    public static bluetoothDeviceHandler getInstance() {
        return bluetoothDeviceHandler.bluetoothDeviceHandler;
    }

    private BluetoothDevice bluetoothDevice;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public bluetoothDeviceHandler(BluetoothDevice bluetoothDevice, BluetoothSocket bluetoothSocket, InputStream inputStream, OutputStream outputStream) {
        this.bluetoothDevice = bluetoothDevice;
        this.bluetoothSocket = bluetoothSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        bluetoothDeviceHandler = this;
    }

    public String readData() throws IOException {
        String response;
        byte buffer[] = new byte[1024];
        int bytes;
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        bytes = dataInputStream.read(buffer);
        response = new String(buffer, 0, bytes);
        return response;
    }

    public void sendData(String data) throws IOException {
        this.outputStream.write(data.getBytes());
    }

}
