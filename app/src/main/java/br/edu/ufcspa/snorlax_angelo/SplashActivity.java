package br.edu.ufcspa.snorlax_angelo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Icarus on 14/01/2017.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, Aplication.class);
        startActivity(intent);
        finish();
    }
}