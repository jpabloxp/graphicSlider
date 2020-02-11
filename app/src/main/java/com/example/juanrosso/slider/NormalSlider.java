package com.example.juanrosso.slider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.MotionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by juanrosso on 06/06/16.
 */
public class NormalSlider extends View {

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

    public NormalSlider(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 1800;
        y1 = 535;
        y2 = 635;
        yCursor = y1+49;
        targetCursor = 50;
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

        subjectsAge = 33;
        subjectsName = "Celine";
        isMale = false;

        c = Calendar.getInstance();

        // create a rectangle that we'll draw later
        rectangle = new Rect(300, 10, 400, 1170);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 17;
            fileName = "TRAIN_NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }

        datos = "Name;Age;Sex;Condition;Repetition;Position;Overshoot;MovementTime - "+stamp.format(new Date())+"\n";
        writeToFile(datos);
        datos = "";
    }

    @Override
    protected void onDraw (Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        paint.setColor(Color.BLACK);
        canvas.drawRect(rectangle, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(400, 210, 405, 212, paint);
        canvas.drawRect(400, 535, 405, 537, paint);
        canvas.drawRect(400, 735, 405, 737, paint);
        canvas.drawRect(400, 810, 405, 812, paint);


        //CHECK IF CURSOR INSIDE TARGET AREA
        if((yCursor >= targetCursor) && (yCursor <= (targetCursor+56))){

            //Calendar.getInstance().getTimeInMillis();
            //Log.d("myTag", "Inside target cursor : " + targetCursor);

            overshoot = true;
            if(enterTime == 0) enterTime = System.currentTimeMillis();
            else{

                //CHECK IF USER VALIDATED THE TARGET AREA
                if((System.currentTimeMillis() != enterTime) && fingerUp == true){


                    if(movementTime == 0) movementTime = System.currentTimeMillis();
                    else{

                        Log.d("myTag", "numRep: " +  numRep);
                        Log.d("myTag", "Tiempo inicio: " +  movementTime);
                        Log.d("myTag", "Tiempo actual : " +  System.currentTimeMillis());

                        //Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                        datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";NS;"+(65 - numRep)+";"+position+";"+countOvershoot+";"+(System.currentTimeMillis() - movementTime)+"\n";
                        writeToFile(datos);
                        datos = "";
                    }

                    //RELOCATE TARGET AREA
                    if(targetCursor == 50){
                        targetCursor = 1074;
                        position = 'D';
                    }
                    else if(targetCursor == 1074){
                        targetCursor = 50;
                        position = 'U';
                    }
                    overshoot = false;
                    countOvershoot = 0;
                    movementTime = System.currentTimeMillis();

                    numRep--;
                }
            }
        }
        else{
            enterTime = 0;

            //COUNT OVERSHOOTING
            if(overshoot){

                //Log.d("myTag", "OVERSHOOT: " + countOvershoot);
                countOvershoot++;
                overshoot = false;
            }
        }

        //IF REPETITIONS ARE OVER, FINISH PROGRAM
        if(numRep < 1){
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            contx.startActivity(intent);
        }

        //CHECK IF TOUCHED INSIDE CURSOR ZONE
        if(touched ){ //ESTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

            if(dif > 0){

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y2+dif) < 1219){

                    //UPDATE VALUES
                        y1 =y1+dif;
                        y2 =y2+dif;
                        yCursor = yCursor+dif;

                }
                else{
                    y2 = 1218;
                    //y1 = 510;
                    y1 = y2 - 100;
                    yCursor = y2-49;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(300, y1, 400, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((yCursor+dif) > 10){

                    //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        y2 = y2-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif));

                }
                else{

                    yCursor = 10;
                    y1 = yCursor - 49;
                    //y2 = 510;
                    y2 = y1 + 100;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(300, y1, 400, y2, paint);
                touched = false;
            }

        }
        else {

            //PAINT CURSOR
            paint.setColor(Color.GRAY);
            canvas.drawRect(300, y1, 400, y2, paint);
        }

        //PAINT TARGET CURSOR
        paint.setColor(Color.RED);
        canvas.drawRect(400, targetCursor, 500, targetCursor + 56, paint);
        canvas.drawRect(200, targetCursor, 300, targetCursor+56, paint);


        //PAINT WHITE CURSOR LINE
        if(y1 >= 535 && y2 <= 735){ //MIDDLE

            yCursor = y1 + 49;
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y1 < 535){ //ABOVE MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y2 > 735){ //BELOW MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }

        //PAINT ZONE INDICATORS
        if(y1 > 10 && y1 < 210){ //TOP
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
        }
        else if(y1 > 210 && y1 < 535){ //UPPER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
            canvas.drawRect(400, 410, 405, 412, paint);
        }
        else if(y1 > 535 && y2 < 735){ //MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 410, 405, 412, paint);
            canvas.drawRect(400, 610, 405, 612, paint);
        }
        else if(y2 > 735 && y2 < 810){ //LOWER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 610, 405, 612, paint);
            canvas.drawRect(400, 810, 405, 812, paint);
        }
        else if(y2 > 810 && y2 < 1170){ //BOTTOM
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 810, 405, 812, paint);
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
            if(x > 300 && x < 400){

                y = (int)event.getY();

                if(yViejo == 0) yViejo = y;
                else{
                    dif = y - yViejo;

                    Log.d("myTag", "This is the difference: " + dif);
                    Log.d("myTag", "This is the Y: " + y);
                    Log.d("myTag", "This is the Y1: " + y1);
                    Log.d("myTag", "This is the Y2: " + y2);

                    if((dif > 150) || (dif < -150)){
                        dif = 0;
                        extraArea = 0;
                    }

                    yViejo = y;
                }
            }

        }



        invalidate();
        return true;
    }

    /*private void writeToFile(String data) {
        File out;
        OutputStreamWriter outStreamWriter = null;
        FileOutputStream outStream = null;

        out = new File(new File(directory), "prueba_log.txt");

        try {
            if ( out.exists() == false ){
                out.createNewFile();
            }

            outStream = new FileOutputStream(out, true);
            outStreamWriter = new OutputStreamWriter(outStream);

            outStreamWriter.append(data);
            outStreamWriter.flush();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }*/

    private void writeToFile(String data) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();

            Log.d("myTag", "sdCard: " + sdCard.getAbsolutePath());
            Log.d("myTag", "data: " + data);

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_NS");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_NS", fileName);
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

    /*private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(contx.openFileOutput(fileName, Context.MODE_APPEND));
            outputStreamWriter.write(data);
            //outputStreamWriter.write("\n\r");
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }*/

}


