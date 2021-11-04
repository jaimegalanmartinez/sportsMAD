package com.mdp.sportsmad;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.snackbar.Snackbar;
import com.mdp.sportsmad.databinding.ActivityMainBinding;
import com.mdp.sportsmad.model.SportCenterDataset;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

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
        //SportCenterDataset.getInstance().removeAllFavourites();//TODO: Remove on final version
        loadSportCenters();




    }
    private void loadSportCenters(){
        String CONTENT_TYPE_JSON = "application/json";
        SportCenterParser sportCenterParser = new SportCenterParser(this);
        String URL_JSON = "https://datos.madrid.es/egob/catalogo/200186-0-polideportivos.json";
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
                    //Get Favouties
                    //recyclerViewAdapter.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getFavouriteList().size());
                    /*if(binding!=null)
                        binding.messageInfoFavourites.setText("");*/
                }else{
                    //Snackbar.make(binding.recyclerViewFavourites, msg.getData().getString("error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();


                    //Toast.makeText(getContext(),msg.getData().getByteArray("error").toString(),Toast.LENGTH_SHORT);
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(this,handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }
}