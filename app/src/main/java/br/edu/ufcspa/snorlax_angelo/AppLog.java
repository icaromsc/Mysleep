package br.edu.ufcspa.snorlax_angelo;

/**
 * Created by Carlos on 19/12/2015.
 */
import android.util.Log;

public class AppLog {
    public static final String APP_TAG = "app";
    public static final String DATABASE = "database";
    public static final String GOOGLE = "gsign";
    public static final String FACEBOOK = "fbsign";


    public static int logString(String message){
        return Log.i(APP_TAG,message);
    }
}