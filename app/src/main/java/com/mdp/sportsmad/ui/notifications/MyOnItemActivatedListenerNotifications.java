package com.mdp.sportsmad.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;


import java.util.List;

public class MyOnItemActivatedListenerNotifications implements OnItemActivatedListener {

    private static final String TAG = "ListOfItems, MyOnItemActivatedListener";
    private List<com.mdp.sportsmad.model.SportCenter> generalList;
    private Context context;

    public MyOnItemActivatedListenerNotifications(Context context, List<com.mdp.sportsmad.model.SportCenter> generalList) {
        this.context = context;
        this.generalList =generalList;
    }
    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails item,
                                   @NonNull MotionEvent e) {
        Log.d(TAG, "Clicked item with position = " + item.getPosition());
        return true;
    }
}
