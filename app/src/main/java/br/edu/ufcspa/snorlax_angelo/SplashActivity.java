package br.edu.ufcspa.snorlax_angelo;


import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import br.edu.ufcspa.snorlax_angelo.managers.SharedPreferenceManager;
import br.edu.ufcspa.snorlax_angelo.model.UserModel;

/**
 * Created by Icarus on 14/01/2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "br.edu.ufcspa.snorlax_angelo",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        UserModel userModel = SharedPreferenceManager.getSharedInstance().getUserModelFromPreferences();
        if(userModel!=null) {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.putExtra(UserModel.class.getSimpleName(), userModel);
            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        }else{
            Intent intent = new Intent(this, TesteLogin.class);
            //Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        /*Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();*/
    }
}