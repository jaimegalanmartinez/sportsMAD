package dte.masteriot.mdp.sportsmad;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.OnItemActivatedListener;

import com.google.gson.Gson;

import java.util.List;

public class MyOnItemActivatedListener implements OnItemActivatedListener {

    private static final String TAG = "ListOfItems, MyOnItemActivatedListener";
    private List<dte.masteriot.mdp.sportsmad.SportCenter> generalList;
    private Context context;

    public MyOnItemActivatedListener(Context context, List<dte.masteriot.mdp.sportsmad.SportCenter> generalList) {
        this.context = context;
        this.generalList =generalList;
    }
    @SuppressLint("LongLogTag")
    @Override
    public boolean onItemActivated(@NonNull ItemDetailsLookup.ItemDetails item,
                                   @NonNull MotionEvent e) {
        Log.d(TAG, "Clicked item with position = " + item.getPosition());
        Gson gson = new Gson();
        String myJson = gson.toJson(generalList.get(item.getPosition()));
        Intent i = new Intent(context, SportCenterDetails.class);
        i.putExtra("sportCenter", myJson);
        context.startActivity(i);
        return true;
    }
}