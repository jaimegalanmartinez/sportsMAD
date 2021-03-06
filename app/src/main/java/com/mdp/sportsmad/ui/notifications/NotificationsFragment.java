package com.mdp.sportsmad.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
import com.google.gson.Gson;
import com.mdp.sportsmad.utils.AsyncManager;
import com.mdp.sportsmad.utils.CheckerRunnable;
import com.mdp.sportsmad.databinding.FragmentNotificationsBinding;
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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Corresponds to the Notifications tab
 */
public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private EditText editBroker;
    private Button save_broker;
    String serverUri = "tcp://x.x.x.x:1883";
    final String subscriptionTopic = "notifications/";
    MqttAndroidClient mqttAndroidClient;
    String clientId = "ExampleAndroidClient";
    String fileNameDefaultSharedPreferences = "not_preferences";
    String separator ="gsmebkhzvd";//Used to concatenate notifications in a same string in SharedPreferences
    private SelectionTracker tracker;
    private MyAdapterNotifications adapterNotifications;
    private MyOnItemActivatedListenerNotifications onItemActivatedListener;
    RecyclerView mRecyclerView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("NotificationsFragment","reached onViewCreated()");
        //Load saved broker
        SharedPreferences sp = getContext().getSharedPreferences("favourites",getContext().MODE_PRIVATE);
        serverUri=sp.getString("broker","");
        if(!serverUri.equals(""))
            loadMQTT();//If something is stored, then try to connect

        //load previous notifications
        SharedPreferences spnot = getContext().getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String favouritesAll =spnot.getString("notifications","");
        /*favouritesAll="";
        editor.putString("notifications",favouritesAll);
        editor.apply();*/
        String[] favouritesSep =favouritesAll.split(separator);
        List<SportCenterNotification> notificationList= new ArrayList<>();
        Gson gson =new Gson();
        for (String s: favouritesSep){
            if(!s.equals("") && s.charAt(0)=='{'){
                notificationList.add((SportCenterNotification)gson.fromJson(s,SportCenterNotification.class));
            }
        }
        SportCenterDataset.getInstance().setNotificationList(notificationList);
        //Load UI of recycler view
        loadRecyclerView();
        //Buttons
        Button clearAllButton = binding.clearAll;
        clearAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAllSelection();
            }
        });
        //If sport centers are not loaded, it launches a observer to update the UI when they are downloaded.
        if(SportCenterDataset.getInstance().isFilled()==false)
            loadSportCenters();

        editBroker = binding.editBroker; //EditText
        save_broker = binding.saveBroker; //Button
        save_broker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//Save broker address on SharedPreferences
                serverUri=editBroker.getText().toString();
                if(serverUri.equals("")){
                    showMessageSnack("Please, fill the address of the MQTT server. (tcp://x.x.x.x:1883)");
                    SharedPreferences sp = getContext().getSharedPreferences("favourites", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("broker", serverUri);
                    editor.apply();
                }else {
                    SharedPreferences sp = getContext().getSharedPreferences("favourites", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("broker", serverUri);
                    editor.apply();
                    serverUri = sp.getString("broker", "");
                    loadMQTT();
                }
            }
        });

    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        for(SportCenter sportCenter : SportCenterDataset.getInstance().getFavouriteList()){
            try {
                if(mqttAndroidClient!=null)
                    mqttAndroidClient.unsubscribe("notifications/"+sportCenter.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        binding = null;
    }

    /**
     * Displays a Snackbar with the text passed as argument
     * @param message
     */
    private void showMessageSnack(String message){
        if(binding!=null && binding.clearAll!=null)
            Snackbar.make(binding.clearAll, message, Snackbar.LENGTH_LONG).setAction("Action", null).show();
    }

    /**
     * Adds to list a new notification and also stores it in SharedPreferences
     * @param sportCenterNotification notification to add
     */
    private void addToHistory(SportCenterNotification sportCenterNotification) {
        System.out.println("LOG: " + sportCenterNotification);
        SharedPreferences sp = getContext().getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String favouritesAll =sp.getString("notifications","");
        Gson gson =new Gson();
        favouritesAll=favouritesAll+separator+gson.toJson(sportCenterNotification);
        editor.putString("notifications",favouritesAll);
        editor.apply();
        SportCenterDataset.getInstance().addNotification(sportCenterNotification);

        adapterNotifications.notifyDataSetChanged();
        adapterNotifications.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getNotificationList().size()-1);
    }

    /**
     * Subscribes to the topic defined in the argument
     * @param subscriptionTopic
     */
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
                new MyItemKeyProviderNotifications(ItemKeyProvider.SCOPE_MAPPED),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookupNotifications(mRecyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        adapterNotifications.setSelectionTracker(tracker);
    }

    /**
     * Connects to the MQTT broker declared in serverUri variable.
     */
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
                        subscribeToTopic(subscriptionTopic+sportCenter.getId());
                    }
                } else {
                    showMessageSnack("Connected to: " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                showMessageSnack("The Connection was lost");
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
                        subscribeToTopic(subscriptionTopic+sportCenter.getId());
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
    /**
     * Creates an Observer to notify when sport centers are downloaded
     */
    private void loadSportCenters(){
        final AsyncManager asyncManager = new ViewModelProvider(this).get(AsyncManager.class);
        //Observer
        final Observer progressObserver = new Observer<List<SportCenter>>(){
            @Override
            public void onChanged(List<SportCenter> sportCenterList){
                //Update UI elements
                Log.d("NotificationFargment", "Message Received with size = " + sportCenterList.size());

                adapterNotifications.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getFavouriteList().size());
            }
        };
        //Create the observation with the previous observers:
        asyncManager.getProgress().observe(getViewLifecycleOwner(),progressObserver);
        asyncManager.launchBackgroundTask(new CheckerRunnable());
    }
    public void deleteAllSelection() {
        SharedPreferences sp = getContext().getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String favouritesAll="";
        editor.putString("notifications",favouritesAll);
        editor.apply();

        SportCenterDataset spd = SportCenterDataset.getInstance();
        int size = spd.getNotificationList().size();
        spd.removeNotificationList();
        mRecyclerView.getAdapter().notifyDataSetChanged();
        mRecyclerView.getAdapter().notifyItemRangeRemoved(0, size);
    }
}

