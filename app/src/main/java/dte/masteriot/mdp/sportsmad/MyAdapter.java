package dte.masteriot.mdp.sportsmad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<dte.masteriot.mdp.sportsmad.MyViewHolder> {

    private static final String TAG = "ListOfItems, MyAdapter";

    private List<dte.masteriot.mdp.sportsmad.SportCenter> items;
    Context context;

    private SelectionTracker selectionTracker;

    public MyAdapter(Context ctxt, List<dte.masteriot.mdp.sportsmad.SportCenter> listofitems) {
        super();
        context = ctxt;
        items = listofitems;
    }

    @Override
    public dte.masteriot.mdp.sportsmad.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // this method has to actually inflate the item view and return the view holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new dte.masteriot.mdp.sportsmad.MyViewHolder(context, v);
    }


    @Override
    public void onBindViewHolder(dte.masteriot.mdp.sportsmad.MyViewHolder holder, int position) {
        // this method actually gives values to the elements of the view holder
        // (values corresponding to the item in 'position')
        final dte.masteriot.mdp.sportsmad.SportCenter item = items.get(position);
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
