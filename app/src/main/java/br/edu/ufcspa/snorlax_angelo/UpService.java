package br.edu.ufcspa.snorlax_angelo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import br.edu.ufcspa.snorlax_angelo.view.UploadFileAsync;

/**
 * Created by icaromsc on 01/02/2017.
 */

public class UpService extends Service {

    private static Timer timer = new Timer();
    private Context ctx;
    private static final String AUDIO_RECORDER_FOLDER = "Snore_angELO";

    //
    private Integer counter=1000 * 60;

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
            Log.d("snorlax_service","running service...");
            processFilesToBeUploaded();
        }
    }

    public void onDestroy()
    {
        Log.d("snorlax_service","destroying service...");
        super.onDestroy();
        //Toast.makeText(this, "Service Stopped ...", Toast.LENGTH_SHORT).show();

    }



    private ArrayList<RecordedFiles> getFiles(){
        DataBaseAdapter data = DataBaseAdapter.getInstance(ctx);
        return (ArrayList<RecordedFiles>) data.getRecordedFilesToBeUploaded();
    }

    private void processFilesToBeUploaded(){
        ArrayList<RecordedFiles> recordedFiles= new ArrayList<RecordedFiles>();
        recordedFiles=getFiles();
        if(recordedFiles.size()>0){
            for (RecordedFiles r: recordedFiles
                 ) {
                uploadFile(r);
            }
        }
    }

    private void uploadFile(RecordedFiles r){
        String filename = r.getFilename();
        Log.d("snorlax_service","starting upload " + r.getFilename()+ " async mode...");
        new UploadFileAsync().execute(filename,String.valueOf(r.getIdRecordedFile()));
    }

}

