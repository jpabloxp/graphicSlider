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
public class dynamicGain extends View {

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

    public dynamicGain(Context context, int sizeX, int sizeY) {
        super(context);
        contx = context;

        x = 700;
        y = 800;
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

        touched = false;
        fingerUp = true;
        op = false;
        overshoot = false;
        training = true;

        datos = "";
        position = 'U';

        subjectsAge = 27;
        subjectsName = "Prueba";
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
            fileName = "TRAIN_DS_"+subjectsName+"_"+stamp.format(new Date())+".txt";
        }
        else{

            numRep = 65;
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
        canvas.drawRect(400, 320, 405, 325, paint);
        canvas.drawRect(400, 420, 405, 425, paint);
        canvas.drawRect(400, 520, 405, 525, paint);
        canvas.drawRect(400, 660, 405, 665, paint);
        canvas.drawRect(400, 760, 405, 765, paint);
        canvas.drawRect(400, 860, 405, 865, paint);
        canvas.drawRect(295, 320, 300, 325, paint);
        canvas.drawRect(295, 420, 300, 425, paint);
        canvas.drawRect(295, 520, 300, 525, paint);
        canvas.drawRect(295, 660, 300, 665, paint);
        canvas.drawRect(295, 760, 300, 765, paint);
        canvas.drawRect(295, 860, 300, 865, paint);

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
                        datos = subjectsName+";"+subjectsAge+";"+(isMale ? "Male" : "Female")+";"+(33 - numRep)+";"+position+";"+countOvershoot+";"+(System.currentTimeMillis() - movementTime)+"\n";
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
        if(touched ){

            if(dif > 0){

                //CHECK IF OPERATING INSIDE CONTROL RANGE
                if((y2+dif) < 1219){
                    if(y1 >= 520 && y2 <= 660){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif;
                        y1 =y2-100;
                        yCursor = yCursor+dif;

                        Log.d("myTag", "AQUI 2: " + y1);
                    }
                    else if(y1 < 320){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1+dif+16;
                        yCursor = yCursor+dif+16;
                        Log.d("myTag", "AQUI +16: " + y1);
                    }
                    else if(y1 < 420){ //MIDDLE
                        //UPDATE VALUES
                        y1 =y1+dif+8;
                        yCursor = yCursor+dif+8;
                        Log.d("myTag", "AQUI +8: " + y1);
                    }
                    else if(y1 < 520){ //MIDDLE
                        //UPDATE VALUES
                        y1 =y1+dif+4;
                        yCursor = yCursor+dif+4;
                        Log.d("myTag", "AQUI +4: " + y1);
                    }
                    else if(y2 > 660){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif+4;
                        yCursor = yCursor+dif+4;
                        Log.d("myTag", "AQUI +4: " + y2);
                    }
                    else if(y2 > 760){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif+8;
                        yCursor = yCursor+dif+8;
                        Log.d("myTag", "AQUI +8: " + y2);
                    }
                    else if(y2 > 860){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2+dif+16;
                        yCursor = yCursor+dif+16;
                        Log.d("myTag", "AQUI +16: " + y2);
                    }

                }
                else{
                    y2 = 1218;
                    //y1 = 545;
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
                    if(y1 >= 520 && y2 <= 660){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif));
                        y2 = y1+100;
                        yCursor = yCursor-(Math.abs(dif));

                        Log.d("myTag", "AQUI 1: " + y1);
                    }
                    else if(y1 < 320){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif))-16;
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif))-16;
                        Log.d("myTag", "AQUI -16: " + y1);
                    }
                    else if(y1 < 420){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif))-8;
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif))-8;
                        Log.d("myTag", "AQUI -8: " + y1);
                    }
                    else if(y1 < 520){ //MIDDLE
                        //UPDATE VALUES
                        y1 = y1-(Math.abs(dif))-4;
                        //y2 = 509;
                        yCursor = yCursor-(Math.abs(dif))-4;
                        Log.d("myTag", "AQUI -4: " + y1);
                    }
                    else if(y2 > 660){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif))-4;
                        yCursor = yCursor-(Math.abs(dif))-4;
                        Log.d("myTag", "AQUI -4: " + y2);
                    }
                    else if(y2 > 760){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif))-8;
                        yCursor = yCursor-(Math.abs(dif))-8;
                        Log.d("myTag", "AQUI -8: " + y2);
                    }
                    else if(y2 > 860){ //MIDDLE
                        //UPDATE VALUES
                        y2 =y2-(Math.abs(dif))-16;
                        yCursor = yCursor-(Math.abs(dif))-16;
                        Log.d("myTag", "AQUI -16: " + y2);
                    }

                }
                else{

                    yCursor = 10;
                    y1 = yCursor - 49;
                    //y2 = 725;
                    Log.d("myTag", "AFUERA: " + y1);
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
        if(y1 >= 520 && y2 <= 660){ //MIDDLE

            yCursor = y1 + 49;
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y1 < 520){ //ABOVE MIDDLE
            paint.setColor(Color.WHITE);
            canvas.drawRect(200, yCursor, 500, yCursor+2, paint);
        }
        else if(y2 > 660){ //BELOW MIDDLE
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
            if(x > 200 && x < 500){

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
