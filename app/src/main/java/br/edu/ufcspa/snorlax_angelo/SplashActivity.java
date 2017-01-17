package br.edu.ufcspa.snorlax_angelo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;
import br.edu.ufcspa.snorlax_angelo.model.UserModel;

/**
 * Created by Icarus on 14/01/2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserModel userModel = SharedPreferenceManager.getSharedInstance().getUserModelFromPreferences();
        if(userModel!=null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(UserModel.class.getSimpleName(), userModel);
            startActivity(intent);
            finishAffinity();
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();*/
    }
}