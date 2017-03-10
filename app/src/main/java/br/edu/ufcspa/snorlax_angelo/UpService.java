package br.edu.ufcspa.snorlax_angelo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import br.edu.ufcspa.snorlax_angelo.database.DataBaseAdapter;
import br.edu.ufcspa.snorlax_angelo.model.RecordedFiles;
import br.edu.ufcspa.snorlax_angelo.model.Recording;
import br.edu.ufcspa.snorlax_angelo.view.UploadFileAsync;
import br.edu.ufcspa.snorlax_angelo.view.UploadFilesAsync;

/**
 * Created by icaromsc on 01/02/2017.
 */

public class UpService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;
    private static final String AUDIO_RECORDER_FOLDER = "Snore_angELO";
    MainTask myTask= new MainTask();;
    //
    private Integer counter=1000 * 60;
    private boolean threadActive=false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void onCreate()
    {
        super.onCreate();
        Log.d("snorlax_service","on create service...");
        ctx = this;
        if(!threadActive)
            startService();
    }

    private void startService()
    {
        Log.d("snorlax_service","start service...");
        timer.scheduleAtFixedRate(myTask, 0, counter);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("snorlax_service","start thread looping...");
                threadActive=true;
                while (true){
                    try {
                        Thread.sleep(counter);
                        process();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    protected class MainTask extends TimerTask
    {
        public void run()
        {
            //Toast.makeText(ctx, "test", Toast.LENGTH_SHORT).show();
            Log.d("snorlax_service","running Timer task...");
            if(isOnline()){
                processFilesToBeUploaded();
                DataBaseAdapter data = DataBaseAdapter.getInstance(ctx);
                data.updateStatusRecordingOnUploadFilesFinished();
            }
            else {
                Log.d("snorlax_service", "device offline...");
                /*Intent intent = new Intent(ctx, UpService.class);
                stopService(intent);*/
                //cancel();
            }
        }
    }


    public void process(){
        Log.d("snorlax_service","running service process...");
        if(isOnline())
            processFilesToBeUploaded();
        else {
            Log.d("snorlax_service", "device offline...");
                /*Intent intent = new Intent(ctx, UpService.class);
                stopService(intent);*/
        }
    }

    public void onDestroy()
    {
        Log.d("snorlax_service","destroying service...");
        //Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();
        myTask=null;
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("snorlax_service","finishing service...");
        return super.stopService(name);
    }

    private ArrayList<RecordedFiles> getFiles(){
        DataBaseAdapter data = DataBaseAdapter.getInstance(ctx);
        return (ArrayList<RecordedFiles>) data.getRecordedFilesToBeUploaded();
    }

    private void processFilesToBeUploaded(){
        ArrayList<RecordedFiles> recordedFiles= new ArrayList<RecordedFiles>();
        recordedFiles=getFiles();
        if(recordedFiles.size()>0){
            Log.d("snorlax_service","files to be uploaded:"+recordedFiles.size());
            RecordedFiles[] files = new RecordedFiles[recordedFiles.size()];
            recordedFiles.toArray(files);
            new UploadFilesAsync().execute(files);
        }
    }

    private void uploadFile(RecordedFiles r){
        String filename = r.getFilename();
        Log.d("snorlax_service","starting upload " + r.getFilename()+ " async mode...");
        new UploadFileAsync().execute(filename,String.valueOf(r.getIdRecordedFile()));
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }
}

