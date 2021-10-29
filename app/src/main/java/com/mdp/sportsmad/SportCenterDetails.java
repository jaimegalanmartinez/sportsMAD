package com.mdp.sportsmad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import java.util.HashSet;
import java.util.Set;

public class SportCenterDetails extends AppCompatActivity {
    private SportCenter sportCenter;
    private TextView title;
    private TextView type;
    private Button urlRelation;
    private TextView street ;
    private Button latLng;
    private TextView schedule;
    private TextView services;
    private CheckBox favorite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_center_details);

        Gson gson = new Gson();
        sportCenter = gson.fromJson(getIntent().getStringExtra("sportCenter"), SportCenter.class);

        title= (TextView) findViewById(R.id.name);
        title.setText(sportCenter.getTitle());
        type= (TextView) findViewById(R.id.type_details);
        type.setText(sportCenter.getType());
        urlRelation= (Button) findViewById(R.id.url_link_details);
        urlRelation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
               Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(sportCenter.getUrlRelation()));
               startActivity(i);
            }
        });
        street= (TextView) findViewById(R.id.street_detail);
        street.setText(sportCenter.getStreet());
        latLng= (Button) findViewById(R.id.button_location);
        latLng.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String myJson = gson.toJson(sportCenter);
                Intent i = new Intent(SportCenterDetails.this, com.mdp.sportsmad.MapsActivity.class);
                i.putExtra("sportCenter", myJson);
                startActivity(i);

            }
        });
        schedule= (TextView) findViewById(R.id.schedule);
        schedule.setText(sportCenter.getSchedule());
        services= (TextView) findViewById(R.id.services);
        services.setText(sportCenter.getServices());

        favorite = (CheckBox) findViewById(R.id.favorite_details);
        //If it is favourite, set checkbox
        if( SportCenterDataset.getInstance().isFavourite(sportCenter.getId())){
            favorite.setChecked(true);
        }
        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(favorite.isChecked()){
                    //Save this sport center as favourite
                    SharedPreferences sharedPreferences = getSharedPreferences("favourites",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
                    favouritesSet.add(Integer.toString(sportCenter.getId()));
                    editor.putStringSet("favourites",favouritesSet);
                    editor.commit();
                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.addFavourite(sportCenter);
                }else{
                    SharedPreferences sharedPreferences = getSharedPreferences("favourites",MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
                    favouritesSet.remove(Integer.toString(sportCenter.getId()));
                    editor.putStringSet("favourites",favouritesSet);
                    editor.commit();
                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.removeFavourite(sportCenter);
                }

            }
        });
    }
}