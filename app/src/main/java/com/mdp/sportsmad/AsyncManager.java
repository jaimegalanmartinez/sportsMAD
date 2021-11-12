package com.mdp.sportsmad;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AsyncManager extends ViewModel {
    private MutableLiveData<List<SportCenter>> sportCentersGeneral;
    private Handler handler;
    String logTag;  // to clearly identify logs
    ExecutorService es;
    public AsyncManager(){
        es = Executors.newSingleThreadExecutor();
        logTag = "Thread name = " + Thread.currentThread().getName() +
                ", Thread id = " + Thread.currentThread().getId() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);
        //configure handler for between ViewModel and Runnable
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                Log.d(logTag, "Message Received with value");
                if(inputMessage.getData().getInt("sportCenters", -1)==1)
                    sportCentersGeneral.setValue(SportCenterDataset.getInstance().getGeneralList());
            }
        };
        sportCentersGeneral= new MutableLiveData<>();
    }
    public LiveData<List<SportCenter>> getProgress(){
        if(sportCentersGeneral==null){
            sportCentersGeneral= new MutableLiveData<>();
        }
        return sportCentersGeneral;
    }

    public void launchBackgroundTask(CheckerRunnable task){
        task.setHandler(handler);
        es.execute(task);
    }
    public void setSportCentersGeneral(List<SportCenter> sportCenters){
        sportCentersGeneral.setValue(sportCenters);
    }
}
