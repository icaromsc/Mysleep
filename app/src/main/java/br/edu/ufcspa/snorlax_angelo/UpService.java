package br.edu.ufcspa.snorlax_angelo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by icaromsc on 01/02/2017.
 */

public class UpService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;
    //
    private Integer counter=5000;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void onCreate()
    {
        super.onCreate();
        ctx = this;
        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, counter);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {
            //Toast.makeText(ctx, "test", Toast.LENGTH_SHORT).show();
            Log.d("snorlax_service","rodou");
        }
    }

    public void onDestroy()
    {
        Log.d("snorlax_service","destroying service...");
        super.onDestroy();
        //Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();

    }
}

