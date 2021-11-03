package com.mdp.sportsmad.ui.notifications;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.widget.RecyclerView;

import com.mdp.sportsmad.R;
import com.mdp.sportsmad.model.SportCenterNotification;

public class MyViewHolderNotifications extends RecyclerView.ViewHolder {

    // Holds references to individual item views
    Context context;
    TextView title;
    TextView description;

    private static final String TAG = "ListOfItems, MyViewHolderNotifications";

    public MyViewHolderNotifications(Context ctxt, View itemView) {
        super(itemView);
        context = ctxt;
        title = itemView.findViewById(R.id.title);
        description = itemView.findViewById(R.id.description);
    }

    void bindValues(SportCenterNotification item, boolean selected) {
        // give values to the elements contained in the item view
        //Default values

        title.setText(item.getTitle());
        if(selected){
            title.setTextColor(Color.RED);
        }
        description.setText(item.getDescription());
    }

    @SuppressLint("LongLogTag")
    @Nullable
    public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {

        Log.d(TAG, "getItemDetails() called");

        ItemDetailsLookup.ItemDetails<Long> itemdet = new ItemDetailsLookup.ItemDetails<Long>() {
            @Override
            public int getPosition() {
                Log.d(TAG, "ItemDetailsLookup.ItemDetails<Long>.getPosition() called");
                return getAdapterPosition();
            }

            @Nullable
            @Override
            public Long getSelectionKey() {
                Log.d(TAG, "ItemDetailsLookup.ItemDetails<Long>.getSelectionKey() called");
                return (long) (getAdapterPosition());
            }
        };

        return itemdet;
    }
}