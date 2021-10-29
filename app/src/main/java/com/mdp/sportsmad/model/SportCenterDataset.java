package com.mdp.sportsmad.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SportCenterDataset {
    private List<SportCenter> generalList;
    private List<SportCenter> favouriteList;
    private static SportCenterDataset instance=null;
    private Context c;
    private SharedPreferences sharedPreferences;
    public SportCenterDataset(Context c){
        generalList = new ArrayList<>();
        favouriteList = new ArrayList<>();
        this.c=c;
        instance=this;
    }
    public static SportCenterDataset createInstance(Context c){
        if(instance==null){
            instance =new SportCenterDataset(c);
        }
        return instance;
    }
    public static SportCenterDataset getInstance(){

        return instance;

    }
    public void setGeneralList(List<SportCenter> generalList) {
        this.generalList = generalList;
        updateFavourites();
    }
    public void updateFavourites(){
        sharedPreferences = c.getSharedPreferences("favourites",MODE_PRIVATE);
        favouriteList.clear();
        Set<String> favouritesSet = sharedPreferences.getStringSet("favourites",new HashSet<>());
        for(String id:favouritesSet)
            favouriteList.add(findSPById(id));

    }
    public void setFavouriteList(List<SportCenter> favouriteList) {
        this.favouriteList = favouriteList;
    }
    public List<SportCenter> getFavouriteList(){
        return favouriteList;
    }
    public void addFavourite(SportCenter sportCenter){
        favouriteList.add(sportCenter);
    }
    public void removeFavourite(SportCenter sportCenter) {
        favouriteList.remove(sportCenter);
    }
    public List<SportCenter> getGeneralList(){
        return generalList;
    }
    private SportCenter findSPById(String id){
        SportCenter sp=null;
        for(SportCenter spg: generalList){
            if(spg.getId()==Integer.parseInt(id)){
                sp=spg;
                break;
            }
        }
        return sp;
    }
    public boolean isFavourite(int id){
        String str_id= Integer.toString(id);
        SportCenter sp =findSPById(str_id);
        return favouriteList.contains(sp);
    }
}
