package com.mdp.sportsmad.activities;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mdp.sportsmad.R;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class SportCenterDetailsActivity extends AppCompatActivity {
    private SportCenter sportCenter;
    private TextView title;
    private TextView type;
    private Button urlRelation;
    private TextView street ;
    private Button latLng;
    private TextView schedule;
    private TextView services;
    private TextView commentary;
    private CheckBox favorite;
    private Button button_send;
    //MQTT
    final String publishTopic = "commentaries/";
    String serverUri = "tcp://192.168.1.29:1883";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";
    String fileNameDefaultSharedPreferences = "fav_preferences";
    private ContextWrapper cw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_center_details);

        Gson gson = new Gson();
        sportCenter = gson.fromJson(getIntent().getStringExtra("sportCenter"), SportCenter.class);
        loadUI();
        loadBrokerMQTT();
        cw =this;

        if(!serverUri.equals(""))
            loadMQTT();
    }
    private void loadMQTT(){

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(this, serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    showMessageSnack("Reconnected to : " + serverURI);
                } else {
                    showMessageSnack("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                showMessageSnack("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {//In this menu, the app doesn't receive any message
                //String id = topic.split("/")[1];
                //SportCenterNotification scn = new SportCenterNotification(Integer.parseInt(id),SportCenterDataset.getInstance().findSPById(id).getTitle(),new String(message.getPayload()));
                //addToHistory(scn);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);

                    //No subscriptions in this option, only send MQTT messages

                    /*for(SportCenter sportCenter : scd.getFavouriteList()){
                        subscribeToTopic("notifications/"+sportCenter.getId());
                    }*/
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    showMessageSnack("Failed to connect to: " + serverUri +
                            ". Cause: " + ((exception.getCause() == null)?
                            exception.toString() : exception.getCause()));
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            showMessageSnack(e.toString());
        }
    }
    public void publishMessage(String topic, String messagePayload) {
        MqttMessage message = new MqttMessage();
        message.setPayload(messagePayload.getBytes());
        message.setRetained(false);
        message.setQos(0);
        Log.d("MQTT send","Topic: "+topic+", Message: "+messagePayload);
        try {
            mqttAndroidClient.publish(topic, message);
            showMessageSnack("Message Published");
        } catch (Exception e) {
            e.printStackTrace();
            showMessageSnack(e.toString());
        }
        if (!mqttAndroidClient.isConnected()) {
            showMessageSnack("Client not connected!");
        }
    }
    private void showMessageSnack(String message){
        Snackbar.make(findViewById(R.id.all_parameters), message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
    private void loadBrokerMQTT(){
        SharedPreferences sp = this.getSharedPreferences("favourites",this.MODE_PRIVATE);
        serverUri=sp.getString("broker","");
    }
    private void loadUI(){

        title= findViewById(R.id.name); //TextView
        title.setText(sportCenter.getTitle());
        type= findViewById(R.id.type_details); //TextView

        switch (sportCenter.getType()) {
            case "Piscinas":
                type.setText("Swimming pool");
                break;
            case "Gimnasios":
                type.setText("Gym");
                break;
            case "Rocodromo":
                type.setText("Climbing wall");
                break;
            case "CamposEstadiosFutbol":
                type.setText("Football stadium");
                break;
            case "Embarcaderos":
                type.setText("Pier");
                break;
            case "PistasTenisBadminton":
                type.setText("Badminton & tennis court");
                break;
            case "CanchasBaloncesto":
                type.setText("Basketball court");
                break;
            default:
                type.setText("Gym");
        }
        urlRelation = findViewById(R.id.url_link_details); //button
        urlRelation.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(sportCenter.getUrlRelation()));
                startActivity(i);
            }
        });
        street= findViewById(R.id.street_detail); //TextView
        street.setText(sportCenter.getStreet());
        latLng= findViewById(R.id.button_location); //Button
        latLng.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String myJson = gson.toJson(sportCenter);
                Intent i = new Intent(SportCenterDetailsActivity.this, MapsActivity.class);
                i.putExtra("sportCenter", myJson);
                startActivity(i);

            }
        });
        schedule= findViewById(R.id.schedule); //TextView
        schedule.setText(sportCenter.getSchedule());
        services= findViewById(R.id.services); //TextView
        services.setText(sportCenter.getServices());

        favorite = findViewById(R.id.favorite_details); //Checkbox
        //If it is favourite, set checkbox
        if(SportCenterDataset.getInstance().isFavourite(sportCenter.getId())){
            favorite.setChecked(true);
        }
        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(favorite.isChecked()){
                    SharedPreferences sp = cw.getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    String favouritesString = sp.getString("StringFavourites","");
                    String[] favouritesSep =favouritesString.split("/");
                    favouritesString=favouritesString+"/"+sportCenter.getId();

                    editor.putString("StringFavourites", favouritesString);
                    editor.apply();

                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.addFavourite(sportCenter);
                }else{

                    SharedPreferences sp = cw.getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    String favouritesString =sp.getString("StringFavourites","");
                    String[] favouritesSep =favouritesString.split("/");
                    favouritesString="";
                    for(String sp1:  favouritesSep) {
                        if(!Integer.toString(sportCenter.getId()).equals(sp1))
                            favouritesString=favouritesString+"/"+sp1;
                    }
                    editor.putString("StringFavourites",favouritesString);
                    editor.apply();

                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.removeFavourite(sportCenter.getId());
                }

            }
        });
        commentary = findViewById(R.id.comment_edit); //TextView

        button_send = findViewById(R.id.button_send); //Button
        button_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(serverUri.equals(""))
                    showMessageSnack("Please, fill the address of the MQTT server. (tcp:x.x.x.x:1883)");
                else {
                    publishMessage(publishTopic + sportCenter.getId(), commentary.getText().toString());
                }
            }
        });
    }
}