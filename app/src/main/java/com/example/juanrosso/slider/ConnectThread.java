package com.example.juanrosso.slider;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by juanrosso on 02/01/17.
 */
public class ConnectThread extends Thread{
    public final BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public ConnectThread(BluetoothDevice device) {
        // Use a temporary object that is later assigned to mmSocket,
        // because mmSocket is final
        BluetoothSocket tmp = null;
        mmDevice = device;

        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            // MY_UUID is the app's UUID string, also used by the server code
            tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {

            Log.d("CONNECTTHREAD", "1Could not close connection:" + e.toString());
        }
        mmSocket = tmp;
    }

    public void run() {
        // Cancel discovery because it will slow down the connection
        //mBluetoothAdapter.cancelDiscovery();
        try {
            // Connect the device through the socket. This will block
            // until it succeeds or throws an exception
            if(!mmSocket.isConnected()) mmSocket.connect();
            else Log.d("CONNECTTHREAD", "4Could not close connection:");
        } catch (IOException connectException) {
            Log.d("CONNECTTHREAD", "2Could not close connection:" + connectException.toString());
            // Unable to connect; close the socket and get out
            try {
                mmSocket.close();
            } catch (IOException closeException) { Log.d("CONNECTTHREAD", "3Could not close connection:" + closeException.toString());}
            return;
        }

        // Do work to manage the connection (in a separate thread)
        // manageConnectedSocket(mmSocket);
    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) { }
    }
}