package com.example.juanrosso.slider;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Set;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Display display = getWindowManager().getDefaultDisplay();
        int width = display.getWidth();  // deprecated
        int height = display.getHeight();  // deprecated

        super.onCreate(savedInstanceState);

        //This are different types of a graphical slider. The standard slider should be NormalSlider. I'm not sure
        //why I have several versions, I assume the latest is the best (either 3 or 4), test them. ClutchSlider is the one
        //I used for my paper. If I remember well, mappedSlider and MicroMoveSlider also work well. I don't remember exactly
        //what they do, maybe you could check them if you're interested. I don't remember what DS and SMDS do, and I think
        //I never finished developing velocitySlider.

        //setContentView(new DS(this, width, height));
        //setContentView(new SMDS(this, width, height));

        //setContentView(new NormalSlider3(this, width, height));
        //setContentView(new NormalSlider2(this, width, height));
        //setContentView(new mappedSlider(this, width, height));
        //setContentView(new microMoveSlider(this, width, height));
        setContentView(new clutchSlider(this, width, height));
        //setContentView(new velocitySlider(this, width, height));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
