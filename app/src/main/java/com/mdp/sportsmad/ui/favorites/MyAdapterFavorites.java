package com.mdp.sportsmad.ui.favorites;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.mdp.sportsmad.R;

import java.util.List;

public class MyAdapterFavorites extends RecyclerView.Adapter<MyViewHolderFavorites> {

    private static final String TAG = "ListOfItems, MyAdaFav";

    private List<com.mdp.sportsmad.model.SportCenter> items;
    Context context;

    private SelectionTracker selectionTracker;

    public MyAdapterFavorites(Context ctxt, List<com.mdp.sportsmad.model.SportCenter> listofitems) {
        super();
        context = ctxt;
        items = listofitems;
    }

    @Override
    public MyViewHolderFavorites onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new MyViewHolderFavorites(context, v);
    }


    @Override
    public void onBindViewHolder(MyViewHolderFavorites holder, int position) {
        // this method actually gives values to the elements of the view holder
        // (values corresponding to the item in 'position')
        final com.mdp.sportsmad.model.SportCenter item = items.get(position);
        holder.bindValues(item);
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
