package br.edu.ufcspa.snorlax_angelo;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.facebook.FacebookSdk;
import com.facebook.drawee.backends.pipeline.Fresco;

import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;

/**
 * Created by Icarus on 14/01/2017.
 */

public class App extends Application implements Application.ActivityLifecycleCallbacks {



    public void onCreate() {
        super.onCreate();
        instantiateManagers();
    }

    /**
     * Method to instantiate all the managers in this app
     */
    private void instantiateManagers() {
        FacebookSdk.sdkInitialize(this);
        Fresco.initialize(this);
        SharedPreferenceManager.getSharedInstance().initiateSharedPreferences(getApplicationContext());
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
