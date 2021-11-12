package com.mdp.sportsmad.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.mdp.sportsmad.model.SportCenterDataset;


public class CheckerRunnable implements Runnable{
    private Handler handler;
    public CheckerRunnable(){
    }
    @Override
    public void run(){
        Message msg;
        Bundle msg_data;
        SportCenterDataset spd = SportCenterDataset.getInstance();
        while (!spd.isFilled()){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        msg = handler.obtainMessage(); // message to send to the UI thread
        msg_data = msg.getData(); // message data
        msg_data.putInt("sportCenters", 1); // (key, value = progress)
        msg.sendToTarget(); // send the message to the target
    }
    public void setHandler(Handler h){
        handler=h;
    }
}
