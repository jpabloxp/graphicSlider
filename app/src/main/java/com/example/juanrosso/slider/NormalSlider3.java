package com.example.juanrosso.slider;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

import android.os.Handler;
import android.widget.Toast;

/**
 * Created by juanrosso on 06/06/16.
 */
public class NormalSlider3 extends View {

    private Rect rectangle;
    private Paint paint;

    int x;
    int y;
    int y1;
    int y2;
    int yCursor;
    int targetCursor;
    int size;
    int dif;
    int yViejo;
    int numRep;
    int subjectsAge;
    int countOvershoot;
    int extraArea;

    long enterTime;
    long movementTime;

    boolean touched;
    boolean fingerUp;
    boolean op;
    boolean isMale;
    boolean overshoot;
    boolean training;

    String datos;
    String subjectsName;
    String fileName;

    char position;

    Context contx;
    Calendar c;

    private OutputStream outputStream;
    private InputStream inStream;
    BluetoothSocket socket;
    ConnectThread conexion;
    ConnectedThread conexion2;

    public NormalSlider3(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 1800;
        y1 = 910;
        y2 = 1010;
        yCursor = y1+49;
        targetCursor = 382;
        size = 100;
        dif = 0;
        yViejo = 0;
        enterTime = 0;
        movementTime = 0;
        countOvershoot = 0;
        numRep = 0;
        extraArea = 0;

        touched = false;
        fingerUp = true;
        op = false;
        overshoot = false;
        training = false;

        datos = "";
        position = 'U';

        subjectsAge = 23;
        subjectsName = "Vivien";
        isMale = true;

        c = Calendar.getInstance();

        try {
            BTconnection();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }

        // create a rectangle that we'll draw later
        rectangle = new Rect(468, 2, 612, 1917);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 6;
            fileName = "TRAIN_NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }

        datos = "Name;Age;Sex;Condition;Repetition;Position;Overshoot;MovementTime - "+stamp.format(new Date())+"\n";
        //writeToFile(datos);
        datos = "";

    }

    @Override
    protected void onDraw (Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        canvas.drawRect(rectangle, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(463, 599, 468, 604, paint);
        canvas.drawRect(612, 599, 617, 604, paint);
        canvas.drawRect(463, 1321, 468, 1325, paint);
        canvas.drawRect(612, 1321, 617, 1325, paint);

        //IF REPETITIONS ARE OVER, FINISH PROGRAM
        if(numRep < 1){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contx.startActivity(intent);
        }

        //CHECK IF TOUCHED INSIDE CURSOR ZONE
        if(touched ){ //ESTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

            if(y > 960){//MIDDLE OF FAoT

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y+20) < 1917){

                    y1 =y-50;
                    y2 =y+50;
                }
                else{
                    y2 = 1966;
                    //y1 = 510;
                    y1 = y2 - 100;
                    yCursor = y2-49;

//                    conexion2.cancel();
                    try {
                        outputStream.close();
                        socket.close();

                        System.exit(0);
                    } catch (Exception e) {Log.e("myTag","Error closing socket");}
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(468, y1, 612, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y-20) > 2){

                    y1 =y-50;
                    y2 =y+50;
                }
                else{

                    yCursor = 2;
                    y1 = yCursor - 49;
                    //y2 = 510;
                    y2 = y1 + 100;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(468, y1, 612, y2, paint);
                touched = false;
            }

        }
        else {

            //PAINT CURSOR
            paint.setColor(Color.GRAY);
            canvas.drawRect(468, y1, 612, y2, paint);
        }

        //PAINT WHITE CURSOR LINE
        paint.setColor(Color.WHITE);
        canvas.drawRect(368, yCursor, 712, yCursor+2, paint);


        String data;
        data = String.valueOf(y);
        try {
            write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        int eventAction = event.getAction();
        int yx = (int)event.getY();

        if((eventAction == MotionEvent.ACTION_DOWN) && (yx > y1 && yx < y2)){
            fingerUp = false;
        }
        else if(eventAction == MotionEvent.ACTION_UP){
            fingerUp = true;
        }
        else if((eventAction == MotionEvent.ACTION_MOVE) && (fingerUp == false)){
            touched = true;
            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 468 && x < 612){

                y = (int)event.getY();
                yCursor = y;
            }

        }

        invalidate();
        return true;
    }

    private void writeToFile(String data) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();

            Log.d("myTag", "sdCard: " + sdCard.getAbsolutePath());
            Log.d("myTag", "data: " + data);

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_NS2");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_NS2", fileName);
            FileOutputStream fOut = new FileOutputStream(file, true);

            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }


    private void BTconnection() throws IOException {
        BluetoothAdapter blueAdapter = null;
        try {
            blueAdapter = BluetoothAdapter.getDefaultAdapter();
            if(!blueAdapter.isEnabled()){
                blueAdapter.enable();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if(bondedDevices.size() > 0) {
                    Object[] devices = (Object []) bondedDevices.toArray();

                    Log.d("myTag", "sdCard: " + devices.length);
                    Log.d("myTag", "sdCard: " + devices[0]);
                    Log.d("myTag", "sdCard: " + devices[1]);
                    BluetoothDevice device = (BluetoothDevice) devices[1];
                    ParcelUuid[] uuids = device.getUuids();
                    //Log.d("myTag", "sdCard: " + uuids[0].getUuid());
                    UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

                    try {
                        socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                        //socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
                    } catch (Exception e) {Log.e("myTag","Error creating socket");}

                    try {
                        socket.connect();
                        outputStream = socket.getOutputStream();
                        Log.e("myTag","Connected at first");
                    } catch (IOException e) {
                        Log.e("myTag",e.getMessage());
                        try {
                            Log.e("myTag","trying fallback...");

                            socket =(BluetoothSocket) device.getClass().getMethod("createRfcommSocket", new Class[] {int.class}).invoke(device,4);
                            socket.connect();
                            outputStream = socket.getOutputStream();

                            Log.e("myTag","Connected from fallback");
                        }
                        catch (Exception e2) {
                            Log.e("myTag", "Couldn't establish Bluetooth connection!");
                        }
                    }
                    /*socket.connect();
                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();*/

                    /*ConnectThread conexion = new ConnectThread(device);
                    conexion.run();
                    Log.d("myTag", "medio: ");
                    conexion.cancel();
*/
                    /*ConnectedThread conexion2 = new ConnectedThread(conexion.mmSocket);
                    conexion2.run();
                    conexion2.write("123");*/
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public void write(String s) throws IOException {
        Log.e("myTag","Sending: "+ s);
        outputStream.write(s.getBytes());
    }

}


