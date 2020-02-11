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
public class DS extends View {

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

    public DS(Context context, int sizeX, int sizeY) {
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

        // create a rectangle that we'll draw later
        rectangle = new Rect(468, 237, 612, 1682);

        // create the Paint and set its color
        paint = new Paint();
        paint.setColor(Color.GRAY);

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


        //CHECK IF TOUCHED INSIDE CURSOR ZONE
        if(touched ){ //ESTOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO

            if(y > 960){//MIDDLE OF FAoT

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y+50) < 1732){

                    y1 =y-50;
                    y2 =y+50;
                }
                else{
                    y2 = 1731;
                    //y1 = 510;
                    y1 = y2 - 100;
                    yCursor = y2-49;
                }

                //PAINT CURSOR
                paint.setColor(Color.GRAY);
                canvas.drawRect(468, y1, 612, y2, paint);
                touched = false;
            }
            else{

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y-50) > 237){

                    y1 =y-50;
                    y2 =y+50;
                }
                else{

                    yCursor = 237;
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

}


