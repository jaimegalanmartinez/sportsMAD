package com.mdp.sportsmad.ui.sportcenters;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mdp.sportsmad.DownloadRunnable;
import com.mdp.sportsmad.MyAdapter;

import com.mdp.sportsmad.SportCenterParser;
import com.mdp.sportsmad.databinding.FragmentSportsCentersBinding;
import com.mdp.sportsmad.model.SportCenter;
import com.mdp.sportsmad.model.SportCenterDataset;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SportCentersFragment extends Fragment {

    private SportCentersViewModel sportCentersViewModel;
    private FragmentSportsCentersBinding binding;

    private static final List<SportCenter> generalList = new ArrayList<>();
    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private static final String CONTENT_TYPE_JSON = "application/json";
    private SportCenterParser sportCenterParser = new SportCenterParser(getContext());
    private static final String URL_JSON = "https://datos.madrid.es/egob/catalogo/200186-0-polideportivos.json";
    private MyOnItemActivatedListener onItemActivatedListener;
    //private SportCenterDataset SportCenterDataset;
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
        loadRecyclerView();
        loadSportCenters();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void loadRecyclerView(){
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
        //Handler to receive Sport Centers
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                String string_result;
                super.handleMessage(msg);
                Log.d(logTag, "message received from background thread");
                if(msg.getData().getBoolean("result")) {
                    SportCenterDataset.getInstance().setGeneralList(sportCenterParser.getParse());
                    recyclerViewAdapter.notifyDataSetChanged();
                    //binding.messageInfo.setText("");
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(getContext(),handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }
}