
/*

//NORMAL ONE
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
        y = 800;
        y1 = 450;
        y2 = 550;
        yCursor = y1+49;
        targetCursor = 50;
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
        training = false;

        datos = "";
        position = 'U';

        subjectsAge = 27;
        subjectsName = "Lea";
        isMale = false;

        c = Calendar.getInstance();

        // create a rectangle that we'll draw later
        rectangle = new Rect(300, 10, 400, 1010);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 17;
            fileName = "TRAIN_NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 33;
            fileName = "NS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }

        datos = "Name;Age;Sex;Repetition;Position;Overshoot;MovementTime - "+stamp.format(new Date())+"\n";
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
        canvas.drawRect(400, 410, 405, 412, paint);
        canvas.drawRect(400, 610, 405, 612, paint);
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

                        Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                        datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";"+(33 - numRep)+";"+position+";"+countOvershoot+";"+(System.currentTimeMillis() - movementTime)+"\n";
                        writeToFile(datos);
                        datos = "";
                    }

                    //RELOCATE TARGET AREA
                    if(targetCursor == 50){
                        targetCursor = 914;
                        position = 'D';
                    }
                    else if(targetCursor == 914){
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
        if(touched && (y > y1 && y < y2)){

            if(dif > 0){

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y2+dif) < 1059){

                    //UPDATE VALUES
                    y1 =y1+dif;
                    y2 =y2+dif;
                    yCursor = yCursor+dif;

                }
                else{
                    y2 = 1058;
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
        if(y1 >= 410 && y2 <= 610){ //MIDDLE

            yCursor = y1 + 49;
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y1 < 410){ //ABOVE MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y2 > 610){ //BELOW MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }

        //PAINT ZONE INDICATORS
        if(y1 > 10 && y1 < 210){ //TOP
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
        }
        else if(y1 > 210 && y1 < 410){ //UPPER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
            canvas.drawRect(400, 410, 405, 412, paint);
        }
        else if(y1 > 410 && y2 < 610){ //MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 410, 405, 412, paint);
            canvas.drawRect(400, 610, 405, 612, paint);
        }
        else if(y2 > 610 && y2 < 810){ //LOWER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 610, 405, 612, paint);
            canvas.drawRect(400, 810, 405, 812, paint);
        }
        else if(y2 > 810 && y2 < 1010){ //BOTTOM
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 810, 405, 812, paint);
        }

    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        int eventAction = event.getAction();


        if(eventAction == MotionEvent.ACTION_DOWN){
            fingerUp = false;
        }
        else if(eventAction == MotionEvent.ACTION_UP){
            fingerUp = true;
        }
        else if(eventAction == MotionEvent.ACTION_MOVE){
            touched = true;
            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 300 && x < 400){

                y = (int)event.getY();

                if(yViejo == 0) yViejo = y;
                else{
                    dif = y - yViejo;
                    if((dif > 50) || (dif < -50)) dif = 0;

                    yViejo = y;
                }

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

}





//DEFORMABLE ONE

public class DeformableSlider extends View {

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

    public DeformableSlider(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 800;
        y1 = 450;
        y2 = 550;
        yCursor = y1+49;
        targetCursor = 50;
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
        training = false;

        datos = "";
        position = 'U';

        subjectsAge = 27;
        subjectsName = "Lea";
        isMale = false;

        c = Calendar.getInstance();

        // create a rectangle that we'll draw later
        rectangle = new Rect(300, 10, 400, 1010);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

        SimpleDateFormat stamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

        if(training){

            numRep = 17;
            fileName = "TRAIN_DS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 33;
            fileName = "DS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }

        datos = "Name;Age;Sex;Repetition;Position;Overshoot;MovementTime - "+stamp.format(new Date())+"\n";
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
        canvas.drawRect(400, 410, 405, 412, paint);
        canvas.drawRect(400, 610, 405, 612, paint);
        canvas.drawRect(400, 810, 405, 812, paint);

        //CHECK WHERE THE CURSOR IS AND SET SIZE FOR THE BOUND CLOSEST TO THE MIDDLE
        if(y1 > 10 && y1 < 110){ //TOP
            y2 = 600;
        }
        else if(y1 > 110 && y1 < 210){ //UPPER MIDDLE 3
            y2 = 570;
        }
        else if(y1 > 210 && y1 < 310){ //UPPER MIDDLE 2
            y2 = 540;
        }
        else if(y1 > 310 && y1 < 410){ //UPPER MIDDLE 1
            y2 = 510;
        }
        else if(y1 > 410 && y2 < 610){ //MIDDLE
            y2 = y1 + 100;
        }
        else if(y2 > 610 && y2 < 710){ //LOWER MIDDLE 1
            y1 = 510;
        }
        else if(y2 > 710 && y2 < 810){ //LOWER MIDDLE 2
            y1 = 480;
        }
        else if(y2 > 810 && y2 < 910){ //LOWER MIDDLE 3
            y1 = 450;
        }
        else if(y2 > 910 && y2 < 1010){ //BOTTOM
            y1 = 420;
        }

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

                        Log.d("myTag", "DIRECCION: " + contx.getFilesDir());
                        datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";"+(33 - numRep)+";"+position+";"+countOvershoot+";"+(System.currentTimeMillis() - movementTime)+"\n";
                        writeToFile(datos);
                        datos = "";
                    }

                    //RELOCATE TARGET AREA
                    if(targetCursor == 50){
                        targetCursor = 914;
                        position = 'D';
                    }
                    else if(targetCursor == 914){
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
        if(touched && (y > y1 && y < y2)){

            if(dif > 0){

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y2+dif) < 1059){
                    if(y1 >= 410 && y2 <= 610){ //MIDDLE
                        //UPDATE VALUES
                        y1 =y1+dif;
                        y2 =y2+dif;
                        yCursor = yCursor+dif;
                    }
                    else if(y2 > 610){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif;
                        yCursor = yCursor+dif;
                    }
                    else if(y1 < 410){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1+dif;
                        yCursor = yCursor+dif;
                    }

                }
                else{
                    y2 = 1058;
                    //y1 = 510;
                    y1 = 420;
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
                    if(y1 >= 410 && y2 <= 610){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        y2 = y2-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif));
                    }
                    else if(y1 < 410){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif));
                    }
                    else if(y2 > 610){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif));
                        yCursor = yCursor-(Math.abs(dif));
                    }

                }
                else{

                    yCursor = 10;
                    y1 = yCursor - 49;
                    //y2 = 510;
                    y2 = 600;
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
        if(y1 >= 410 && y2 <= 610){ //MIDDLE

            yCursor = y1 + 49;
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y1 < 410){ //ABOVE MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y2 > 610){ //BELOW MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }

        //PAINT ZONE INDICATORS
        if(y1 > 10 && y1 < 210){ //TOP
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
        }
        else if(y1 > 210 && y1 < 410){ //UPPER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 210, 405, 212, paint);
            canvas.drawRect(400, 410, 405, 412, paint);
        }
        else if(y1 > 410 && y2 < 610){ //MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 410, 405, 412, paint);
            canvas.drawRect(400, 610, 405, 612, paint);
        }
        else if(y2 > 610 && y2 < 810){ //LOWER MIDDLE
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 610, 405, 612, paint);
            canvas.drawRect(400, 810, 405, 812, paint);
        }
        else if(y2 > 810 && y2 < 1010){ //BOTTOM
            paint.setColor(Color.GREEN);
            canvas.drawRect(400, 810, 405, 812, paint);
        }

    }

    @Override
    public boolean onTouchEvent (MotionEvent event)
    {
        int eventAction = event.getAction();


        if(eventAction == MotionEvent.ACTION_DOWN){
            fingerUp = false;
        }
        else if(eventAction == MotionEvent.ACTION_UP){
            fingerUp = true;
        }
        else if(eventAction == MotionEvent.ACTION_MOVE){
            touched = true;
            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 300 && x < 400){

                y = (int)event.getY();

                if(yViejo == 0) yViejo = y;
                else{
                    dif = y - yViejo;
                    if((dif > 50) || (dif < -50)) dif = 0;

                    yViejo = y;
                }

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

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_DS");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_DS", fileName);
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


*/