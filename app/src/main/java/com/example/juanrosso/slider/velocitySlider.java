package com.example.juanrosso.slider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by juanrosso on 06/06/16.
 */
public class velocitySlider extends View {

    private Rect rectangle;
    private Paint paint;

    int x;
    int y;
    int p;
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

    float cdgain;

    long oldTime;
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

    public velocitySlider(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 800;
        p = 60;
        y1 = 535;
        y2 = 635;
        yCursor = 585;
        targetCursor = 50;
        size = 100;
        dif = 0;
        yViejo = 0;
        cdgain = 0;
        oldTime = 0;
        enterTime = 0;
        movementTime = 0;
        countOvershoot = 0;
        numRep = 0;

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
            fileName = "TRAIN_DGS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "DGS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
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
        canvas.drawRect(400, 320, 405, 325, paint);
        canvas.drawRect(400, 430, 405, 435, paint);
        canvas.drawRect(400, 760, 405, 765, paint);
        canvas.drawRect(400, 870, 405, 875, paint);
        canvas.drawRect(295, 320, 300, 325, paint);
        canvas.drawRect(295, 430, 300, 435, paint);
        canvas.drawRect(295, 760, 300, 765, paint);
        canvas.drawRect(295, 870, 300, 875, paint);

        //CHECK IF CURSOR INSIDE TARGET AREA
        if((yCursor >= targetCursor) && (yCursor <= (targetCursor+56))){

            //Calendar.getInstance().getTimeInMillis();
            //Log.d("myTag", "Inside target cursor : " + targetCursor);

            overshoot = true;
            if(enterTime == 0) enterTime = System.currentTimeMillis();
            else{

                //CHECK IF USER VALIDATED THE TARGET AREA
                if((System.currentTimeMillis() - enterTime) > 300){

                    datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";DGS;"+(65 - numRep)+";"+position+";"+countOvershoot+";"+((System.currentTimeMillis() - 300) - movementTime)+"\n";

                    Log.d("myTag", "numRep: " +  numRep);
                    Log.d("myTag", "Tiempo inicio: " +  movementTime);
                    Log.d("myTag", "Tiempo actual : " +  System.currentTimeMillis());

                    //Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                    //writeToFile(datos);
                    datos = "";

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
        if(touched ){

            if(dif > 0){

                if(cdgain > 0){
                    if((y2+(dif*cdgain)) < 1219){
                        if(yCursor > 590){

                            //UPDATE VALUES
                            yCursor = yCursor+(int)(dif*cdgain);
                            y2 =yCursor+50;
                            y1 =y1+dif;
                        }
                        else{

                            if((y1+(dif*cdgain)) < (y2-100)){

                                //UPDATE VALUES
                                yCursor = yCursor+(int)(dif*cdgain);
                                y1 =yCursor-50;
                                y2 =y2+dif;
                            }
                            else{

                                yCursor =yCursor+(int)(dif*cdgain);
                                y1 =y1+dif;
                                y2 =yCursor+50;
                            }
                        }
                    }
                    else{
                        y2 = 1218;
                        //y1 = 545;
                        yCursor = y2-49;
                    }
                }
                else{

                    yCursor =yCursor+dif;
                    y1 =y1+dif;
                    y2 =y2+dif;
                }
                
                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(300, y1, 400, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if(cdgain > 0){
                    if((yCursor-((Math.abs(dif))*cdgain)) > 10){
                        if(yCursor <= 590){

                            //UPDATE VALUES
                            yCursor = yCursor-(int)((Math.abs(dif))*cdgain);
                            y1 =yCursor-50;
                            y2 = y2-(Math.abs(dif));
                        }
                        else{

                            if((y2-((Math.abs(dif))*cdgain)) > y1+100){

                                //UPDATE VALUES
                                yCursor = yCursor-(int)((Math.abs(dif))*cdgain);
                                y2 =yCursor+50;
                                y1 = y1-(Math.abs(dif));
                            }
                            else{

                                yCursor = yCursor-((int)((Math.abs(dif))*cdgain));
                                y2 = y2-(Math.abs(dif));
                                y1 =yCursor-50;
                            }

                        }
                    }
                    else{

                        yCursor = 10;
                        y1 = yCursor - 49;
                        //y2 = 725;
                        Log.d("myTag", "AFUERA: " + y1);
                    }
                }
                else{

                    yCursor = yCursor-(Math.abs(dif));
                    y1 =y1-(Math.abs(dif));
                    y2 =y2-(Math.abs(dif));
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
        canvas.drawRect(200, targetCursor, 300, targetCursor + 56, paint);

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
        if(y1 > 10 && y1 < 320){ //TOP
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 320, 300, 325, paint);
            canvas.drawRect(400, 320, 405, 325, paint);
        }
        else if(y1 > 320 && y1 < 420){ //MIDDLE TOP 2
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 320, 300, 325, paint);
            canvas.drawRect(400, 320, 405, 325, paint);
            canvas.drawRect(295, 420, 300, 425, paint);
            canvas.drawRect(400, 420, 405, 425, paint);
        }
        else if(y1 > 420 && y1 < 520){ //MIDDLE TOP 1
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 420, 300, 425, paint);
            canvas.drawRect(400, 420, 405, 425, paint);
            canvas.drawRect(295, 520, 300, 525, paint);
            canvas.drawRect(400, 520, 405, 525, paint);
        }
        else if(y1 >= 520 && y2 <= 660){ //MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 520, 300, 525, paint);
            canvas.drawRect(400, 520, 405, 525, paint);
            canvas.drawRect(295, 660, 300, 665, paint);
            canvas.drawRect(400, 660, 405, 665, paint);
        }
        else if(y2 >= 660 && y2 <= 760){ //MIDDLE BOTTOM 1
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 660, 300, 665, paint);
            canvas.drawRect(400, 660, 405, 665, paint);
            canvas.drawRect(295, 760, 300, 765, paint);
            canvas.drawRect(400, 760, 405, 765, paint);
        }
        else if(y2 >= 760 && y2 <= 860){ //MIDDLE BOTTOM 2
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 760, 300, 765, paint);
            canvas.drawRect(400, 760, 405, 765, paint);
            canvas.drawRect(295, 860, 300, 865, paint);
            canvas.drawRect(400, 860, 405, 865, paint);
        }
        else if(y2 > 860 && y2 < 1170){ //BOTTOM
            paint.setColor(Color.GREEN);
            canvas.drawRect(295, 860, 300, 865, paint);
            canvas.drawRect(400, 860, 405, 865, paint);
        }
    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        int eventAction = event.getAction();
        int yx = (int)event.getY();
        float velocity = 0;

        if((eventAction == MotionEvent.ACTION_DOWN) && (yx > y1 && yx < y2)){
            fingerUp = false;
        }
        else if(eventAction == MotionEvent.ACTION_UP){
            fingerUp = true;
        }
        else if(eventAction == MotionEvent.ACTION_MOVE){

            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 200 && x < 500){

                touched = true;
                y = (int)event.getY();

                if(yViejo == 0){
                    yViejo = y;
                    oldTime = System.currentTimeMillis();
                }
                else{
                    dif = y - yViejo;
                    Log.d("myTag", "This is the dP: " + (long)(Math.abs(dif)));
                    Log.d("myTag", "This is the dT: " + ((System.currentTimeMillis() - oldTime)));
                    if((Math.abs(dif)) > 0) velocity = (float)(((Math.abs(dif))/(System.currentTimeMillis() - oldTime)));
                    Log.d("myTag", "This is the velocity: " + velocity);

                    if((dif > 60) || (dif < -60)) dif = 0;
                    yViejo = y;
                    oldTime = System.currentTimeMillis();
                }

                /*long map(long x, long in_min, long in_max, long out_min, long out_max)
                long map(long x, long 590, long 1170, long 10, long 590)
                {
                    return (x - 590) * (590 - 10) / (1170 - 590) + 10;
                }
                {
                    return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
                }
                if((p > 0) && (yCursor <= 590)) cdgain = (int)(2.5 * (float)(580.0/p));
                else if((p > 0) && (yCursor > 590)) cdgain = (int)(10 * (float)(p/580.0));*/

                cdgain = (float)(velocity*1.5);

                //Log.d("myTag", "This is the difference: " + dif);
                //Log.d("myTag", "This is the Y: " + y);
            }

            //Log.d("myTag", "This is the y: " + y);
        }

        invalidate();
        return true;
    }

    private void writeToFile(String data) {
        try {
            File sdCard = Environment.getExternalStorageDirectory();

            Log.d("myTag", "sdCard: " + sdCard.getAbsolutePath());
            Log.d("myTag", "data: " + data);

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_DGS3");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_DGS3", fileName);
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

}