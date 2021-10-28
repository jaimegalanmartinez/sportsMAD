package com.mdp.sportsmad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.mdp.sportsmad.model.SportCenter;

public class SportCenterDetails extends AppCompatActivity {
    private SportCenter sportCenter;
    private TextView title;
    private TextView type;
    private Button urlRelation;
    private TextView street ;
    private Button latLng;
    private TextView schedule;
    private TextView services;

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
                /*Intent i = new Intent(SportCenterDetails.this, com.mdp.sportsmad.MapsActivity.class);
                i.putExtra("sportCenter", myJson);
                startActivity(i); //TODO add mapActivity class
                */
            }
        });
        schedule= (TextView) findViewById(R.id.schedule);
        schedule.setText(sportCenter.getSchedule());
        services= (TextView) findViewById(R.id.services);
        services.setText(sportCenter.getServices());

    }
}