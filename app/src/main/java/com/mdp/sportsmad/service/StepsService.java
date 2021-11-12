package com.mdp.sportsmad.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**     AndroidManifest.xml:
 *         <service android:name="org.eclipse.paho.android.service.MqttService"/>
 *         <service android:name=".service.StepsService"
 *             android:enabled="true"
 *             android:exported="false"/>
 *         <receiver android:name=".service.Receiver"/>
 * Due to lack of time we could not add the service, we tried to add it to count the steps in the background.
 */
public class StepsService extends Service implements SensorEventListener {
    private static final String LOG_TAG = "StepsService";
    private SensorManager sensorManager;
    private Sensor stepSensor;
    private boolean isRunning = false;
    private int stepCount;
    private String fileNameDefaultSharedPreferences;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

       /* try {
            isRunning = true;
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if (stepSensor != null){
                sensorManager.registerListener(this,stepSensor,SensorManager.SENSOR_DELAY_NORMAL);
                //ContextCompat.startForegroundService(getApplicationContext(),intent);

            }

        }catch (Exception e){
            Log.e("ERROR", e.getMessage());
        }*/
        //return super.onStartCommand(intent, flags, startId);
        return START_STICKY; //Keep service running even if the app is killed


    }

    @Override
    public boolean stopService(Intent name) {
    return super.stopService(name);
    }
    /*public void createNotificationChannel(){

        //Need to check if OS is OREO(8.0) or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel("ChannelId1", "Foreground notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }

     */

    //SensorEventListener interface

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        fileNameDefaultSharedPreferences = getApplicationContext().getPackageName() + "_preferences";
        if (sensorEvent.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(isRunning){
                //sensorEvent.values[0] = 1 if step detected
                stepCount = (int) (stepCount + sensorEvent.values[0]);
                SharedPreferences prefs = getSharedPreferences(fileNameDefaultSharedPreferences, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("stepCount",stepCount);
                editor.apply();
                Log.d("STEPSCOUNTSERVICE:",String.valueOf(stepCount));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        Intent i = new Intent(this, Receiver.class);
        sendBroadcast(i);
        super.onDestroy();
    }

}
