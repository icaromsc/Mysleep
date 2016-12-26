package br.edu.ufcspa.snorlax_angelo;

/**
 * Created by Carlos on 19/12/2015.
 */
import android.util.Log;

public class AppLog {
    private static final String APP_TAG = "AudioRecorderActivity";

    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}