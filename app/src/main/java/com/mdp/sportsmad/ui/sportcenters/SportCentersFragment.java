package com.mdp.sportsmad.ui.sportcenters;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdp.sportsmad.utils.AsyncManager;
import com.mdp.sportsmad.utils.CheckerRunnable;

import com.mdp.sportsmad.databinding.FragmentSportsCentersBinding;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Describes the Sport Centers tab
 */
public class SportCentersFragment extends Fragment {

    private FragmentSportsCentersBinding binding;

    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private MyOnItemActivatedListener onItemActivatedListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSportsCentersBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("SportCentersFragment","reached onViewCreated()");
        loadRecyclerView();//Loads UI of recycler view
        if(!SportCenterDataset.getInstance().isFilled())
            loadSportCenters();
        else
            binding.messageInfo.setText("");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadRecyclerView(){
        Log.d("SportCentersFragment","reached loadRecyclerView()");
        //recyclerView = findViewById(R.id.recyclerView);
        recyclerView = binding.recyclerView;
        recyclerViewAdapter = new MyAdapter(getContext(), SportCenterDataset.getInstance().getGeneralList());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Tracker
        onItemActivatedListener = new MyOnItemActivatedListener(getContext(),SportCenterDataset.getInstance().getGeneralList());
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProvider(ItemKeyProvider.SCOPE_MAPPED),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookup(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListener)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);
        recyclerViewAdapter.notifyDataSetChanged();
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
                Log.d(logTag, "Message Received with size = " + sportCenterList.size());
                recyclerViewAdapter.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getGeneralList().size());                if(binding!=null)
                    binding.messageInfo.setText("");
            }
        };
        //Create the observation with the previous observers:
        asyncManager.getProgress().observe(getViewLifecycleOwner(),progressObserver);
        asyncManager.launchBackgroundTask(new CheckerRunnable());
    }
}