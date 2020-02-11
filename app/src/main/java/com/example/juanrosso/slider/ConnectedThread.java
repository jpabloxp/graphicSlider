package com.example.juanrosso.slider;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by juanrosso on 02/01/17.
 */
public class ConnectedThread extends Thread {
    public static String ipAddress;// ur ip
    private static int portNumber = 12000;// portnumber

    private Socket client;

    private OutputStreamWriter printwriter;
    private String message;

    public void run() {
        // TODO Auto-generated method stub
        try {
            client = new Socket(ipAddress, portNumber);
            printwriter = new OutputStreamWriter(client.getOutputStream(), "ISO-8859-1");
        }

        catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String s) {
        try {
            printwriter.write("any message");
            printwriter.flush();
        } catch (IOException e) { }
    }

    /* Call this from the main activity to shutdown the connection */
    public void cancel() {
        try {
            printwriter.close();
            client.close();
        } catch (IOException e) { }
    }
}