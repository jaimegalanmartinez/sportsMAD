package com.mdp.sportsmad.ui.favorites;

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
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.databinding.FragmentFavoritesBinding;
import com.mdp.sportsmad.model.SportCenter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private FavoritesViewModel favoritesViewModel;
    private FragmentFavoritesBinding binding;

    private static final List<SportCenter> generalList = new ArrayList<>();
    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapter recyclerViewAdapter;
    private SelectionTracker tracker;

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
        if(SportCenterDataset.getInstance().isFilled()==false)
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
        final AsyncManager asyncManager = new ViewModelProvider(this).get(AsyncManager.class);
        //Observer
        final Observer progressObserver = new Observer<List<SportCenter>>(){
            @Override
            public void onChanged(List<SportCenter> sportCenterList){
                //Update UI elements
                Log.d(logTag, "Message Received with size = " + sportCenterList.size());
                recyclerViewAdapter.notifyItemRangeChanged(0,SportCenterDataset.getInstance().getFavouriteList().size());
                if(binding!=null)
                    binding.messageInfoFavourites.setText("");
            }
        };
        //Create the observation with the previous observers:
        asyncManager.getProgress().observe(getViewLifecycleOwner(),progressObserver);
        asyncManager.launchBackgroundTask(new CheckerRunnable());
    }
}