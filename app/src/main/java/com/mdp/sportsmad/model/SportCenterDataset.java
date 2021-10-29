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
    private static SharedPreferences sharedPreferences;
    public SportCenterDataset(Context c){
        generalList = new ArrayList<>();
        favouriteList = new ArrayList<>();
        this.c=c;
        instance=this;
    }
    public static SportCenterDataset createInstance(Context c){
        if(instance==null){
            instance =new SportCenterDataset(c);
            sharedPreferences = c.getSharedPreferences("favourites",MODE_PRIVATE);
        }
        return instance;
    }
    public static SportCenterDataset getInstance(){

        return instance;

    }
    public void setGeneralList(List<SportCenter> generalListnew) {
        this.generalList.clear();
        for(SportCenter sp: generalListnew)
            this.generalList.add(sp);
        updateFavourites();
    }
    public void updateFavourites(){

        favouriteList.clear();
        Set<String> favouritesSet = sharedPreferences.getStringSet("favourites",new HashSet<>());
        for(String id:favouritesSet)
            favouriteList.add(findSPById(id));

    }
    public void setFavouriteList(List<SportCenter> favouriteListnew) {
        this.favouriteList.clear();
        for(SportCenter sp: favouriteListnew)
            this.favouriteList.add(sp);
    }
    public List<SportCenter> getFavouriteList(){
        return favouriteList;
    }
    public void addFavourite(SportCenter sportCenter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
        favouritesSet.add(Integer.toString(sportCenter.getId()));
        editor.putStringSet("favourites",favouritesSet);
        editor.commit();
        favouriteList.add(sportCenter);
    }
    public void removeFavourite(int id) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
        favouritesSet.remove(Integer.toString(id));
        editor.putStringSet("favourites",favouritesSet);
        editor.commit();
        SportCenter toRemove=null;
        for(SportCenter sp1: favouriteList){
            if(sp1.getId()==id){
                toRemove=sp1;
            }
        }
        if(toRemove!=null)
            favouriteList.remove(toRemove);
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
        boolean contains=false;
        for(SportCenter sp1: favouriteList){
            if(sp1.getId()==id)
                contains=true;
        }
        return contains;
    }
    public void resetFavourites(){
        favouriteList.clear();
    }
    public void removeAllFavourites(){
        favouriteList.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favourites",new HashSet<>() );
        editor.commit();

    }
}
