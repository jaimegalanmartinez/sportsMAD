package com.mdp.sportsmad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        //Calling method OnDestroy on SplashScreen Activity
        finish();
    }
}