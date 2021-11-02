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

    /**
     * Creates an instance on this class and configures the getSharedPreferences store option.
     * @param c context of the application
     * @return returns the instance created
     */
    public static SportCenterDataset createInstance(Context c){
        if(instance==null){
            instance =new SportCenterDataset(c);
            sharedPreferences = c.getSharedPreferences("favourites",MODE_PRIVATE);
        }
        return instance;
    }

    /**
     * Return the single instance of this class to always have a reference to the same elements.
     * @return instance of this class
     */
    public static SportCenterDataset getInstance(){
        return instance;
    }

    /**
     * Updates the generalList with a new one.
     * @param generalListnew new element to include in generalList
     */
    public void setGeneralList(List<SportCenter> generalListnew) {
        this.generalList.clear();
        for(SportCenter sp: generalListnew)
            this.generalList.add(sp);
        updateFavourites();
    }

    /**
     * Reloads from disk the favouriteList.
     * This is executed after loading generalList.
     */
    public void updateFavourites(){

        favouriteList.clear();
        Set<String> favouritesSet = sharedPreferences.getStringSet("favourites",new HashSet<>());
        for(String id:favouritesSet)
            favouriteList.add(findSPById(id));
    }
    /**
     * Updates the favouriteList to a new one
     * @param favouriteListnew new favouriteList to save
     */
    public void setFavouriteList(List<SportCenter> favouriteListnew) {
        this.favouriteList.clear();
        for(SportCenter sp: favouriteListnew)
            this.favouriteList.add(sp);
    }
    public List<SportCenter> getFavouriteList(){
        return favouriteList;
    }
    /**
     * Adds a sport center in favouriteList and in disk.
     * @param sportCenter to add
     */
    public void addFavourite(SportCenter sportCenter){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
        favouritesSet.add(Integer.toString(sportCenter.getId()));
        editor.putStringSet("favourites",favouritesSet);
        editor.commit();
        favouriteList.add(sportCenter);
    }

    /**
     * Deletes a sport center in favouriteList and in disk.
     * @param id identifier to remove that sport center
     */
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

    /**
     * Return the sport center based on its id
     * @param id of the sport center to search
     * @return sport center that matches or null otherwise
     */
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

    /**
     * Return true if the spirt center identified with its id is favourite
     * @param id of the sport center to search
     * @return true if it is favourite
     */
    public boolean isFavourite(int id){
        boolean contains=false;
        for(SportCenter sp1: favouriteList){
            if(sp1.getId()==id) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    /**
     * Deletes all favourites selected in the list.
     */
    public void resetFavourites(){
        favouriteList.clear();
    }
    /**
     * Deletes all favourites selected in the list and in disk.
     * Only for debug purposes.
     */
    public void removeAllFavourites(){
        favouriteList.clear();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet("favourites",new HashSet<>() );
        editor.commit();
    }
}