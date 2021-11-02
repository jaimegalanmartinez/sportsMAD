package com.mdp.sportsmad.ui.favorites;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.selection.ItemKeyProvider;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mdp.sportsmad.DownloadRunnable;
import com.mdp.sportsmad.MyAdapter;
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.SportCenterParser;
import com.mdp.sportsmad.databinding.FragmentFavoritesBinding;
import com.mdp.sportsmad.model.SportCenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;
    private FragmentFavoritesBinding binding;

    private static final List<SportCenter> generalList = new ArrayList<>();
    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;
    private static final String CONTENT_TYPE_JSON = "application/json";
    private SportCenterParser sportCenterParser = new SportCenterParser(getContext());
    private static final String URL_JSON = "https://datos.madrid.es/egob/catalogo/200186-0-polideportivos.json";
    private MyOnItemActivatedListenerFavourites onItemActivatedListenerFavourites;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        /*favoritesViewModel =
                new ViewModelProvider(this).get(FavoritesViewModel.class);
*/
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*final TextView textView = binding.textFavorites;
        favoritesViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("FavoritesFragment","reached onViewCreated()");
        loadRecyclerView();
        if(SportCenterDataset.getInstance().getGeneralList().size()==0)
            loadSportCenters();
        else
            binding.messageInfoFavourites.setText("");
    }
    @Override
    public void onResume() {
        Log.d("FavoritesFragment","reached onResume()");
        super.onResume();
        //Reload teh dataset
        List<SportCenter> FavouriteList = SportCenterDataset.getInstance().getFavouriteList();
        List<SportCenter> FavouriteListCopy= new ArrayList<SportCenter> ();//Create a copy
        for (SportCenter sp: FavouriteList)
            FavouriteListCopy.add(sp);
        int FavouriteList_size=FavouriteList.size();
        //reset the previous dataset
        SportCenterDataset.getInstance().resetFavourites();
        recyclerViewAdapter.notifyItemRangeRemoved(0,FavouriteList_size+1);
        //Update totally to the new one
        SportCenterDataset.getInstance().setFavouriteList(FavouriteListCopy);
        recyclerViewAdapter.notifyItemRangeChanged(0,FavouriteListCopy.size());
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void loadRecyclerView(){
        Log.d("FavoritesFragment","reached loadRecyclerView()");
        //recyclerView = findViewById(R.id.recyclerView);
        recyclerView = binding.recyclerViewFavourites;

        recyclerViewAdapter = new MyAdapter(getContext(), SportCenterDataset.getInstance().getFavouriteList());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Layout
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Tracker
        onItemActivatedListenerFavourites = new MyOnItemActivatedListenerFavourites(getContext(),SportCenterDataset.getInstance().getFavouriteList());
        tracker = new SelectionTracker.Builder<>(
                "my-selection-id",
                recyclerView,
                new MyItemKeyProviderFavourites(ItemKeyProvider.SCOPE_MAPPED),
//                new StableIdKeyProvider(recyclerView), // This caused the app to crash on long clicks
                new MyItemDetailsLookupFavourites(recyclerView),
                StorageStrategy.createLongStorage())
                .withOnItemActivatedListener(onItemActivatedListenerFavourites)
                .build();
        recyclerViewAdapter.setSelectionTracker(tracker);
        recyclerViewAdapter.notifyDataSetChanged();
    }
    private void loadSportCenters(){
        //Handler to receive Sport Centers
        Log.d("FavoritesFragment","reached loadSportCenters()");
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                // message received from background thread: load complete (or failure)
                super.handleMessage(msg);
                Log.d(logTag, "message received from background thread");
                if(msg.getData().getBoolean("result")) {
                    SportCenterDataset.getInstance().setGeneralList(sportCenterParser.getParse());
                    //Get Favouties
                    recyclerViewAdapter.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getFavouriteList().size());
                    if(binding!=null)
                        binding.messageInfoFavourites.setText("");
                }else{
                    Snackbar.make(binding.recyclerViewFavourites, msg.getData().getString("error"), Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    //Toast.makeText(getContext(),msg.getData().getByteArray("error").toString(),Toast.LENGTH_SHORT);
                }
            }
        };
        Executor executor = Executors.newSingleThreadExecutor();
        DownloadRunnable dr = new DownloadRunnable(getContext(),handler,CONTENT_TYPE_JSON,URL_JSON);
        dr.setParser(sportCenterParser);
        executor.execute(dr);
    }
}