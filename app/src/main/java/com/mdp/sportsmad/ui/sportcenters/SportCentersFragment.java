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

import com.mdp.sportsmad.AsyncManager;
import com.mdp.sportsmad.CheckerRunnable;
import com.mdp.sportsmad.MyAdapter;

import com.mdp.sportsmad.databinding.FragmentSportsCentersBinding;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SportCentersFragment extends Fragment {

    private FragmentSportsCentersBinding binding;

    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private MyOnItemActivatedListener onItemActivatedListener;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //sportCentersViewModel =
         //       new ViewModelProvider(this).get(SportCentersViewModel.class);

        binding = FragmentSportsCentersBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        /*sportCentersViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        */
        return root;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("SportCentersFragment","reached onViewCreated()");
        loadRecyclerView();
        if(SportCenterDataset.getInstance().isFilled()==false)
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
    /*private void loadSportCenters(){
        //Handler to receive Sport Centers
        Log.d("SportCentersFragment","reached loadSportCenters()");
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                super.handleMessage(msg);
                Log.d(logTag, "message received from background thread");
                if(msg.getData().getBoolean("result")) {
                    SportCenterDataset.getInstance().setGeneralList(sportCenterParser.getParse());
                    //recyclerViewAdapter.notifyDataSetChanged();
                    recyclerViewAdapter.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getGeneralList().size());
                    if(binding!=null)
                        binding.messageInfo.setText("");
                }else{
                    Snackbar.make(binding.recyclerView, msg.getData().getString("error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    //Toast.makeText(getContext(),msg.getData().getByteArray("error").toString(),Toast.LENGTH_SHORT);
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(getContext(),handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }*/
}