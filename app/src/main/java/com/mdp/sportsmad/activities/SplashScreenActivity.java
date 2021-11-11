package com.mdp.sportsmad.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * Finally not included in the application due to compatibility problems with lower Android versions than Android 10
 * Used the bg_gradient.xml and splash_background.xml
 */
public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
        //Calling method OnDestroy on SplashScreen Activity
        finish();
    }
}