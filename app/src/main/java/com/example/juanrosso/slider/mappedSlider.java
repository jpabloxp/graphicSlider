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
public class mappedSlider extends View {

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

    public mappedSlider(Context context, int sizeX, int sizeY) {
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
            fileName = "TRAIN_MS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
            fileName = "MS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
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
        canvas.drawRect(463, 599, 468, 604, paint);
        canvas.drawRect(612, 599, 617, 604, paint);
        canvas.drawRect(463, 1321, 468, 1325, paint);
        canvas.drawRect(612, 1321, 617, 1325, paint);

        //CHECK IF CURSOR INSIDE TARGET AREA
        if((yCursor >= targetCursor) && (yCursor <= (targetCursor+72))){

            //Calendar.getInstance().getTimeInMillis();
            //Log.d("myTag", "Inside target cursor : " + targetCursor);

            overshoot = true;
            if(enterTime == 0) enterTime = System.currentTimeMillis();
            else{

                //CHECK IF USER VALIDATED THE TARGET AREA
                if((System.currentTimeMillis() - enterTime) > 200){

                    datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";MS;"+(65 - numRep)+";"+position+";"+countOvershoot+";"+((System.currentTimeMillis() - 200) - movementTime)+"\n";

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

            if(y > 960){//MIDDLE OF FAoT

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if(yCursor < 1732){

                    //UPDATE VALUES
                    y1 =yViejo-50;
                    y2 =y+50;
                    yCursor = y;
                }
                else{
                    y2 = 1731;
                    //y1 = y2 - 100;
                    yCursor = y2-49;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(468, y1, 612, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if(yCursor > 237){

                    //UPDATE VALUES
                    y1 =y-50;
                    y2 =yViejo+50;
                    yCursor = y;
                }
                else{

                    yCursor = 236;
                    y1 = yCursor - 49;
                    //y2 = y1 + 100;
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

            //getting the touched x and y position
            x = (int)event.getX();
            if(x > 468 && x < 612){

                touched = true;
                if(yx >= 599 && yx <= 1321){
                    y = (int)event.getY();

                    yViejo = y;
                    y = (y - 599) * (1682 - 237) / (1321 - 599) + 237;
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

            File dir = new File (sdCard.getAbsolutePath() + "/Exp_MS");
            dir.mkdirs();
            File file = new File(sdCard.getAbsolutePath() + "/Exp_MS", fileName);
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
