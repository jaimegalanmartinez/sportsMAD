package com.mdp.sportsmad.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.mdp.sportsmad.DownloadRunnable;
import com.mdp.sportsmad.R;
import com.mdp.sportsmad.SportCenterParser;
import com.mdp.sportsmad.databinding.ActivityMainBinding;
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.service.Receiver;
import com.mdp.sportsmad.service.StepsService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private String CONTENT_TYPE_JSON = "application/json";
    private String URL_JSON = "https://datos.madrid.es/egob/catalogo/200186-0-polideportivos.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile, R.id.navigation_favorites, R.id.navigation_sportscenters, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        SportCenterDataset.createInstance(this,this);//Initialize
        //SportCenterDataset.getInstance().removeAllFavourites();
        if(SportCenterDataset.getInstance().isFilled()==false)
            loadSportCenters();
    }
    private void loadSportCenters(){

        SportCenterParser sportCenterParser = new SportCenterParser(this);

        //Handler to receive Sport Centers
        Log.d("FavoritesFragment","reached loadSportCenters()");
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                super.handleMessage(msg);
                Log.d("sportMAD", "message received from background thread");
                if(msg.getData().getBoolean("result")) {
                    SportCenterDataset.getInstance().setGeneralList(sportCenterParser.getParse());
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(this,handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }

    /*@Override
    protected void onResume() {
        stopService(new Intent(this, StepsService.class));
        super.onResume();
    }
    @Override
    protected void onStop() {
        ContextCompat.startForegroundService(this, new Intent(this, StepsService.class));
        super.onStop();
    }
    */
    @Override
    protected void onPause() {
        super.onPause();
    }
}