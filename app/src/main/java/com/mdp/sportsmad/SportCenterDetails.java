package com.mdp.sportsmad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.model.SportCenterNotification;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Date;
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
    private TextView commentary;
    private CheckBox favorite;
    private Button button_send;
    //MQTT
    final String serverUri = "tcp://192.168.1.29:1883";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";

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
        if(SportCenterDataset.getInstance().isFavourite(sportCenter.getId())){
            favorite.setChecked(true);
        }
        favorite.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(favorite.isChecked()){
                    //Save this sport center as favourite

                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.addFavourite(sportCenter);
                }else{

                    SportCenterDataset spdataset = SportCenterDataset.getInstance();
                    spdataset.removeFavourite(sportCenter.getId());
                }

            }
        });
        commentary = (TextView)  findViewById(R.id.comment_edit);
        loadMQTT();
        button_send = (Button) findViewById(R.id.button_send);
        button_send.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                publishMessage("commentaries/"+sportCenter.getId(),commentary.getText().toString());
            }
        });
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
}