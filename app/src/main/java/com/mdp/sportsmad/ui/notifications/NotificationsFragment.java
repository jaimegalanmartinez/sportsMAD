package com.mdp.sportsmad.ui.notifications;

import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mdp.sportsmad.AsyncManager;
import com.mdp.sportsmad.CheckerRunnable;
import com.mdp.sportsmad.databinding.FragmentNotificationsBinding;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.model.SportCenterNotification;
import com.mdp.sportsmad.ui.sportcenters.MyItemDetailsLookup;
import com.mdp.sportsmad.ui.sportcenters.MyItemKeyProvider;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private FragmentNotificationsBinding binding;

    final String serverUri = "tcp://192.168.1.29:1883";
    //final String subscriptionTopic = "ubuntu/topic";
    final String publishTopic = "android/topic";
    String publishMessage = "Hello World!";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";

    private SelectionTracker tracker;
    private MyAdapterNotifications adapterNotifications;
    private MyOnItemActivatedListenerNotifications onItemActivatedListener;
    RecyclerView mRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);
*/
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textNotifications;
        notificationsViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("NotificationsFragment","reached onViewCreated()");
        loadRecyclerView();
        loadMQTT();
        //Buttons
        Button clearAllButton = binding.clearAll;
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllSelection();
            }
        });
        if(SportCenterDataset.getInstance().getGeneralList().size()==0)
            loadSportCenters();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(SportCenter sportCenter : SportCenterDataset.getInstance().getFavouriteList()){
            try {
                mqttAndroidClient.unsubscribe("notifications/"+sportCenter.getId());
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }

        binding = null;
    }
    private void showMessageSnack(String message){
        if(binding!=null)
            Snackbar.make(binding.recyclerViewNotifications, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }
    private void addToHistory(SportCenterNotification sportCenterNotification) {
        System.out.println("LOG: " + sportCenterNotification);
        SportCenterDataset.getInstance().addNotification(sportCenterNotification);
        adapterNotifications.notifyDataSetChanged();
        adapterNotifications.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getNotificationList().size()-1);
/*
        if(binding!=null)
            Snackbar.make(binding.recyclerViewNotifications, mainText, Snackbar.LENGTH_LONG).setAction("Action", null).show();*/
    }

    public void subscribeToTopic(String subscriptionTopic) {
        try {

            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    showMessageSnack("Subscribed to: " + subscriptionTopic);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    showMessageSnack("Failed to subscribe to: " + subscriptionTopic);
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
            showMessageSnack(e.toString());
        }

    }


    private void loadRecyclerView(){
        mRecyclerView = binding.recyclerViewNotifications;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        adapterNotifications = new MyAdapterNotifications(getContext(),SportCenterDataset.getInstance().getNotificationList());
        mRecyclerView.setAdapter(adapterNotifications);
        onItemActivatedListener = new MyOnItemActivatedListenerNotifications(getContext(),null);
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                mRecyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        adapterNotifications.setSelectionTracker(tracker);
    }
    private void loadMQTT(){

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    showMessageSnack("Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    SportCenterDataset scd =SportCenterDataset.getInstance();
                    for(SportCenter sportCenter : scd.getFavouriteList()){
                        subscribeToTopic("notifications/"+sportCenter.getId());
                    }
                } else {
                    showMessageSnack("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                showMessageSnack("The Connection was lost.");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String id = topic.split("/")[1];
                SportCenterNotification scn = new SportCenterNotification(Integer.parseInt(id),SportCenterDataset.getInstance().findSPById(id).getTitle(),new String(message.getPayload()));
                addToHistory(scn);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        //mqttConnectOptions.setCleanSession(false);
        mqttConnectOptions.setCleanSession(true);
        //mqttConnectOptions.setWill(publishTopic,"disconnected".getBytes(),1,true);

        //addToHistory("ConnLocalBroadcastManagerecting to " + serverUri + "...");
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

                    SportCenterDataset scd =SportCenterDataset.getInstance();
                    //Subscribe to all favourites sport centers
                    for(SportCenter sportCenter : scd.getFavouriteList()){
                        subscribeToTopic("notifications/"+sportCenter.getId());
                    }
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
    private void loadSportCenters(){
        final AsyncManager asyncManager = new ViewModelProvider(this).get(AsyncManager.class);
        //Observer
        final Observer progressObserver = new Observer<List<SportCenter>>(){
            @Override
            public void onChanged(List<SportCenter> sportCenterList){
                //Update UI elements
                Log.d("NotificationFargment", "Message Received with size = " + sportCenterList.size());
                adapterNotifications.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getFavouriteList().size());
                /*if(binding!=null)
                    binding.messageInfoFavourites.setText("");*/
            }
        };
        //Create the observation with the previous observers:
        asyncManager.getProgress().observe(getViewLifecycleOwner(),progressObserver);
        asyncManager.launchBackgroundTask(new CheckerRunnable());
    }
    public void deleteAllSelection() {
        SportCenterDataset spd =SportCenterDataset.getInstance();
        int size = spd.getNotificationList().size();
        spd.removeNotificationList();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.getAdapter().notifyItemRangeRemoved(0,size);
    }
    /*public void (){
        Iterator iterator = tracker.getSelection().iterator();
        //Get setected items
        ArrayList<Integer> listSelected= new ArrayList<Integer>();
        while (iterator.hasNext()){
            listSelected.add(Integer.parseInt(iterator.next().toString()));
        }
        //Order items
        Collections.sort(listSelected);
        Collections.reverse(listSelected);

        for (Integer i: listSelected) {
            //Delete in database
            SportCenterDataset spd =SportCenterDataset.getInstance();
            spd.removeNotification(spd.getNotificationList().get(i.intValue()).getId());
            //delete in RecyclerView
            mRecyclerView.getAdapter().notifyItemRemoved(i.intValue());
            mRecyclerView.getAdapter().notifyItemRangeChanged(i.intValue(),spd.getNotificationList().size());
        }
        //Clear all selected items
        tracker.clearSelection();
    }*/

}

