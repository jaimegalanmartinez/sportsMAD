package com.mdp.sportsmad.ui.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.mdp.sportsmad.R;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterNotification;

import java.util.List;

public class MyAdapterNotifications extends RecyclerView.Adapter<MyViewHolderNotifications> {

    private static final String TAG = "ListOfItems, MyAdapter";

    private List<SportCenterNotification> items;
    Context context;

    private SelectionTracker selectionTracker;

    public MyAdapterNotifications(Context ctxt, List<SportCenterNotification> listofitems) {
        super();
        context = ctxt;
        items = listofitems;
    }

    @Override
    public MyViewHolderNotifications onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new MyViewHolderNotifications(context, v);
    }


    @Override
    public void onBindViewHolder(MyViewHolderNotifications holder, int position) {
        // this method actually gives values to the elements of the view holder
        // (values corresponding to the item in 'position')
        final SportCenterNotification item = items.get(position);
        holder.bindValues(item, selectionTracker.isSelected(holder.getItemDetails().getSelectionKey()));
        Log.d(TAG, "onBindViewHolder() called for element in position " + position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setSelectionTracker(SelectionTracker selectionTracker) {
        this.selectionTracker = selectionTracker;
    }
}
