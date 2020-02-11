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
public class microMoveSlider extends View {

    private Rect rectangle;
    private Paint paint;

    int x;
    int y;
    int y1;
    int y2;
    int yCursor;
    int targetCursor;
    int cdgain;
    int dif;
    int yViejo;
    int numRep;
    int subjectsAge;
    int countOvershoot;

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

    public microMoveSlider(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 800;
        y1 = 910;
        y2 = 1010;
        yCursor = y1+49;
        targetCursor = 382;
        cdgain = 7;
        dif = 0;
        yViejo = 0;
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

        subjectsAge = 23;
        subjectsName = "Vivien";
        isMale = true;

        c = Calendar.getInstance();

        // create a rectangle that we'll draw later
        rectangle = new Rect(468, 237, 612, 1682);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 22;
            fileName = "TRAIN_mMS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "mMS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
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
        canvas.drawRect(463, 599, 468, 601, paint);
        canvas.drawRect(612, 599, 617, 601, paint);
        canvas.drawRect(463, 1321, 468, 1323, paint);
        canvas.drawRect(612, 1321, 617, 1323, paint);

        paint.setColor(Color.GREEN);
        canvas.drawRect(463, 739, 468, 741, paint);
        canvas.drawRect(612, 739, 617, 741, paint);
        canvas.drawRect(463, 1181, 468, 1183, paint);
        canvas.drawRect(612, 1181, 617, 1183, paint);

        //CHECK IF CURSOR INSIDE TARGET AREA
        if((yCursor >= targetCursor) && (yCursor <= (targetCursor+72))){

            //Calendar.getInstance().getTimeInMillis();
            //Log.d("myTag", "Inside target cursor : " + targetCursor);

            overshoot = true;
            if(enterTime == 0) enterTime = System.currentTimeMillis();
            else{

                //CHECK IF USER VALIDATED THE TARGET AREA
                if((System.currentTimeMillis() - enterTime) > 200){

                    datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";mMS;"+(65 - numRep)+";"+position+";"+countOvershoot+";"+((System.currentTimeMillis() - 200) - movementTime)+"\n";

                    Log.d("myTag", "numRep: " +  numRep);
                    Log.d("myTag", "Tiempo inicio: " +  movementTime);
                    Log.d("myTag", "Tiempo actual : " +  System.currentTimeMillis());

                    //Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                    writeToFile(datos);
                    datos = "";

                    //RELOCATE TARGET AREA
                    if(targetCursor == 382){
                        targetCursor = 1450;
                        position = 'D';
                    }
                    else if(targetCursor == 1450){
                        targetCursor = 382;
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

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((yCursor+dif+6) < 1682){
                    if(y1 >= 739 && y2 <= 1181){ //MIDDLE
                        //UPDATE VALUES
                        yCursor = y;
                        y1 = yCursor-50;
                        y2 = y1+100;
                    }
                    else if(y1 < 739){ //MIDDLE
                        //UPDATE VALUES
                        y1 =y1+dif+cdgain;
                        y2 =y2+dif;
                        yCursor = yCursor+dif+cdgain;
                    }
                    else if(y2 > 1181){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif+cdgain;
                        y1 =y1+dif;
                        yCursor = yCursor+dif+cdgain;
                    }

                }
                else{
                    y2 = 1731;
                    //y1 = 770;
                    yCursor = y2-49;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(468, y1, 612, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((yCursor+dif) > 237){
                    if(y1 >= 739 && y2 <= 1181){ //MIDDLE
                        //UPDATE VALUES
                        yCursor = y;
                        y1 = yCursor-50;
                        y2 = y1+100;
                    }
                    else if(y1 < 739){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif))-cdgain;
                        y2 = y2-(Math.abs(dif));
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif))-cdgain;
                    }
                    else if(y2 > 1181){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif))-cdgain;
                        y1 = y1-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif))-cdgain;
                    }

                }
                else{
                    yCursor = 237;
                    y1 = yCursor - 49;
                    //y2 = 1150;
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
        paint.setColor(Color.RED);
        canvas.drawRect(368, targetCursor, 468, targetCursor + 72, paint);
        canvas.drawRect(612, targetCursor, 712, targetCursor+72, paint);

        //PAINT WHITE CURSOR LINE
        paint.setColor(Color.WHITE);
        if(y1 >= 760 && y2 <= 1160){ //MIDDLE
            yCursor = y1+50;
        }
        canvas.drawRect(368, yCursor, 712, yCursor+2, paint);

        //PAINT ZONE INDICATORS
        /*if(y1 > 10 && y1 < 320){ //TOP
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
        }*/
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
        else if(eventAction == MotionEvent.ACTION_MOVE){
            touched = true;
            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 468 && x < 612){

                y = (int)event.getY();

                if(yViejo == 0) yViejo = y;
                else{
                    dif = y - yViejo;
                    if((dif > 60) || (dif < -60)) dif = 0;

                    yViejo = y;
                }

                //Log.d("myTag", "This is the difference: " + dif);
                //Log.d("myTag", "This is the Y: " + y);
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

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_mMS");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_mMS", fileName);
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
