package com.mdp.sportsmad.ui.favorites;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
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
import com.mdp.sportsmad.model.SportCenterDataset;
import com.mdp.sportsmad.databinding.FragmentFavoritesBinding;
import com.mdp.sportsmad.model.SportCenter;

import org.jetbrains.annotations.NotNull;
import java.util.List;


/**
 * Corresponds to the Favourite tab
 */
public class FavoritesFragment extends Fragment {

    private FragmentFavoritesBinding binding;
    private String logTag ="SportsMAD_main";
    private RecyclerView recyclerView;
    private MyAdapterFavorites recyclerViewAdapter;
    private SelectionTracker tracker;
    String fileNameDefaultSharedPreferences = "fav_preferences";
    private MyOnItemActivatedListenerFavourites onItemActivatedListenerFavourites;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("FavoritesFragment","reached onViewCreated()");
        loadRecyclerView();//Loads UI of recycler view
        if(SportCenterDataset.getInstance().isFilled()==false)//If sport centers not downloaded, create Observer
            loadSportCenters();
        else {
            //If thy are loaded, show only favourites
            binding.messageInfoFavourites.setText("");
            List<SportCenter> favouriteList =SportCenterDataset.getInstance().getFavouriteList();
            favouriteList.clear();
            SharedPreferences sp = getContext().getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);
            String favouritesString = sp.getString("StringFavourites","");
            if(favouritesString!="") {
                String[] favouritesSep = favouritesString.split("/");//Separator to concatenate id of favourite sport centers in a same string in SharedPreferences
                for (String id : favouritesSep)
                    if(!id.equals(""))
                        favouriteList.add(SportCenterDataset.getInstance().findSPById(id));//Add favourite to list

            }
        }
    }
    @Override
    public void onResume() {
        Log.d("FavoritesFragment","reached onResume()");
        super.onResume();
        //Reload the dataset
        recyclerViewAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void loadRecyclerView(){
        Log.d("FavoritesFragment","reached loadRecyclerView()");
        recyclerView = binding.recyclerViewFavourites;

        recyclerViewAdapter = new MyAdapterFavorites(getContext(), SportCenterDataset.getInstance().getFavouriteList());
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

    /**
     * Creates an Observer to notify when sport centers are downloaded
     */
    private void loadSportCenters(){
        final AsyncManager asyncManager = new ViewModelProvider(this).get(AsyncManager.class);
        //Observer
        final Observer progressObserver = new Observer<List<SportCenter>>(){
            @Override
            public void onChanged(List<SportCenter> sportCenterList){
                //Read from SharedPreferences teh favourites stored
                Log.d(logTag, "Message Received with size = " + sportCenterList.size());
                List<SportCenter> favouriteList =SportCenterDataset.getInstance().getFavouriteList();
                SharedPreferences sp = getContext().getSharedPreferences(fileNameDefaultSharedPreferences, MODE_PRIVATE);

                String favouritesString = sp.getString("StringFavourites","");
                if(favouritesString!="") {
                    String[] favouritesSep = favouritesString.split("/");
                    for (String id : favouritesSep)
                        if(!id.equals(""))
                            favouriteList.add(SportCenterDataset.getInstance().findSPById(id));

                }
                //Update UI elements
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