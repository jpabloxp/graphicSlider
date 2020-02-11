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
public class clutchSlider extends View {

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

    public clutchSlider(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 800;
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

        touched = false;
        fingerUp = true;
        op = false;
        overshoot = false;
        training = true;

        datos = "";
        position = 'U';

        subjectsAge = 28;
        subjectsName = "Ornela";
        isMale = false;

        c = Calendar.getInstance();

        // create a rectangle that we'll draw later
        rectangle = new Rect(468, 237, 612, 1682);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        //This saves data for the experiment
        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 12;
            fileName = "TRAIN_CS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "CS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }

        datos = "Name;Age;Sex;Condition;Repetition;Position;Overshoot;MovementTime - "+stamp.format(new Date())+"\n";
        writeToFile(datos);
        datos = "";
    }

    @Override
    protected void onDraw (Canvas canvas)
    {
        //Background color
        canvas.drawColor(Color.WHITE);

        //Draw the slider operative area
        paint.setColor(Color.BLACK);
        canvas.drawRect(rectangle, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect(463, 599, 468, 601, paint);
        canvas.drawRect(612, 599, 617, 601, paint);
        canvas.drawRect(463, 1321, 468, 1323, paint);
        canvas.drawRect(612, 1321, 617, 1323, paint);

        paint.setColor(Color.GREEN);
        canvas.drawRect(463, 760, 468, 762, paint);
        canvas.drawRect(612, 760, 617, 762, paint);
        canvas.drawRect(463, 1160, 468, 1162, paint);
        canvas.drawRect(612, 1160, 617, 1162, paint);

        //CHECK WHERE THE CURSOR IS AND SET SIZE FOR THE BOUND CLOSEST TO THE MIDDLE
        /*if(y1 > 10 && y1 < 110){ //TOP
            y2 = 725;
        }
        else if(y1 > 110 && y1 < 210){ //UPPER MIDDLE 3
            y2 = 695;
        }
        else if(y1 > 210 && y1 < 310){ //UPPER MIDDLE 2
            y2 = 665;
        }
        else if(y1 > 310 && y1 < 535){ //UPPER MIDDLE 1
            y2 = 635;
        }
        else if(y1 > 535 && y2 < 735){ //MIDDLE
            y2 = y1 + 100;
        }
        else if(y2 > 735 && y2 < 810){ //LOWER MIDDLE 1
            y1 = 635;
        }
        else if(y2 > 810 && y2 < 910){ //LOWER MIDDLE 2
            y1 = 605;
        }
        else if(y2 > 910 && y2 < 1010){ //LOWER MIDDLE 3
            y1 = 575;
        }
        else if(y2 > 1010 && y2 < 1170){ //BOTTOM
            y1 = 545;
        }*/


        if(y1 > 237 && y1 < 360){ //TOP
            y2 = 1150;
        }
        else if(y1 > 360 && y1 < 460){ //UPPER MIDDLE 3
            y2 = 1087;
        }
        else if(y1 > 460 && y1 < 599){ //UPPER MIDDLE 2
            y2 = 1023;
        }
        else if(y1 > 599 && y1 < 760){ //UPPER MIDDLE 1
            y2 = 960;
        }
        else if(y1 > 760 && y2 < 1160){ //MIDDLE
            y2 = y1 + 100;
        }
        /*else if(y1 >= 760 && y2 <= 1160){ //LOWER MIDDLE 1
            y1 = 510;
        }*/
        else if(y2 > 1160 && y2 < 1321){ //LOWER MIDDLE 1
            y1 = 960;
        }
        else if(y2 > 1321 && y2 < 1460){ //LOWER MIDDLE 2
            y1 = 897;
        }
        else if(y2 > 1460 && y2 < 1560){ //LOWER MIDDLE 3
            y1 = 833;
        }
        else if(y2 > 1560 && y2 < 1682){ //BOTTOM
            y1 = 770;
        }

        //CHECK IF CURSOR IS INSIDE TARGET AREA
        if((yCursor >= targetCursor) && (yCursor <= (targetCursor+72))){

            //Calendar.getInstance().getTimeInMillis();
            //Log.d("myTag", "Inside target cursor : " + targetCursor);

            overshoot = true;
            if(enterTime == 0) enterTime = System.currentTimeMillis();
            else{

                //CHECK IF USER VALIDATED THE TARGET AREA
                if((System.currentTimeMillis() - enterTime) > 200){

                    datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";CS;"+(65 - numRep)+";"+position+";"+countOvershoot+";"+((System.currentTimeMillis() - 200) - movementTime)+"\n";

                    Log.d("myTag", "numRep: " +  numRep);
                    Log.d("myTag", "Tiempo inicio: " +  movementTime);
                    Log.d("myTag", "Tiempo actual : " +  System.currentTimeMillis());

                    //Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                    writeToFile(datos);
                    datos = "";

                    //RELOCATE TARGET AREA
                    if(targetCursor == 382){
                        targetCursor = 1450;
                        //D stands for Down
                        position = 'D';
                    }
                    else if(targetCursor == 1450){
                        targetCursor = 382;
                        //U stands for Up
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
                if((yCursor+dif) < 1682){
                    if(y1 >= 760 && y2 <= 1160){ //MIDDLE
                        //UPDATE VALUES
                        y1 =y1+dif;
                        y2 =y2+dif;
                        yCursor = yCursor+dif;
                    }
                    else if(y2 > 1160){ //LOWER BOUND
                        //UPDATE VALUES
                        y2 =y2+dif;
                        yCursor = yCursor+dif;
                    }
                    else if(y1 < 760){ //UPPER BOUND
                        //UPDATE VALUES
                        y1 = y1+dif;
                        yCursor = yCursor+dif;
                    }

                }
                else{
                    y2 = 1731;
                    y1 = 770;
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
                    if(y1 >= 760 && y2 <= 1160){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        y2 = y2-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif));
                    }
                    else if(y1 < 760){ //UPPER BOUND
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif));
                    }
                    else if(y2 > 1160){ //LOWER BOUND
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif));
                    }

                }
                else{
                    yCursor = 237;
                    y1 = yCursor - 49;
                    y2 = 1150;
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

        //PAINT TARGET CURSOR
        paint.setColor(Color.RED);
        canvas.drawRect(368, targetCursor, 468, targetCursor + 72, paint);
        canvas.drawRect(612, targetCursor, 712, targetCursor+72, paint);

        //PAINT WHITE CURSOR LINE
        paint.setColor(Color.WHITE);
        if(y1 >= 760 && y2 <= 1160){ //MIDDLE
            yCursor = y1+50;
        }
        canvas.drawRect(368, yCursor, 712, yCursor+2, paint);

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

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_CS");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_CS", fileName);
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


