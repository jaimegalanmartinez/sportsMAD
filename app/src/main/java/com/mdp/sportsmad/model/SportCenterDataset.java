package com.mdp.sportsmad.model;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.google.gson.Gson;
import com.mdp.sportsmad.utils.AsyncManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Stores the sport centers downloaded in list and can store and load from SharedPreferences
 * the favourites sport centers and notifications.
 * In addition, it can add favourite or notification and remove them.
 */
public class SportCenterDataset {
    private List<SportCenter> generalList;//All sport centers. At the beginning of the app it is empty until they are downloaded.
    private List<SportCenter> favouriteList;//Only favourites sport centers
    private List<SportCenterNotification> notificationList;//List of notifications shown to hte user
    private static SportCenterDataset instance=null;//Single instance on this class
    private Context c;//Context to save values on SharedPreferences
    private static SharedPreferences sharedPreferences;
    private  ViewModelStoreOwner vm;//ViewModel to notify that sport centers have been loaded
    private boolean filled;//True if generalList is filled
    public SportCenterDataset(Context c,ViewModelStoreOwner vm){
        generalList = new ArrayList<>();
        favouriteList = new ArrayList<>();
        notificationList = new ArrayList<>();
        this.c=c;
        instance=this;
        filled=false;
        this.vm=vm;
    }

    /**
     * Creates an instance on this class and configures the getSharedPreferences store option.
     * @param c context of the application
     * @param viewModelStore
     * @return returns the instance created
     */
    public static SportCenterDataset createInstance(Context c, ViewModelStoreOwner viewModelStore){
        if(instance==null){
            instance =new SportCenterDataset(c,viewModelStore);
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
        //updateFavourites();
        filled=true;
        //Notify that lists are loaded
        final AsyncManager asyncManager = new ViewModelProvider(vm).get(AsyncManager.class);
        asyncManager.setSportCentersGeneral(generalList);
    }

    /**
     * Reloads from disk the favouriteList.
     * This is executed after loading generalList.
     */
    public void updateFavourites(){

        /*favouriteList.clear();
        Set<String> favouritesSet = sharedPreferences.getStringSet("favourites",new HashSet<>());
        for(String id:favouritesSet)
            favouriteList.add(findSPById(id));

         */
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

        favouriteList.add(sportCenter);
    }

    /**
     * Deletes a sport center in favouriteList and in disk.
     * @param id identifier to remove that sport center
     */
    public void removeFavourite(int id) {
        /*SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> favouritesSet =sharedPreferences.getStringSet("favourites",new HashSet<>());
        favouritesSet.remove(Integer.toString(id));
        editor.putStringSet("favourites",favouritesSet);
        editor.apply();
*/
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
    public SportCenter findSPById(String id){
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

    }
    /**
     * Adds a sport center in favouriteList and in disk.
     * @param sportCenterNotification to add
     */
    public void addNotification(SportCenterNotification sportCenterNotification){

        notificationList.add(sportCenterNotification);
    }


    /**
     * Obtain a sport center notification by its id.
     */
    public SportCenterNotification getSportCenterNotificationById(int id){
        SportCenterNotification scnElement = null;
        Gson gson =new Gson();
        for(SportCenterNotification i: notificationList){
            if(i.getId()==id)
                scnElement=i;
        }
        return scnElement;
    }
    public List<SportCenterNotification> getNotificationList(){
        return notificationList;
    }

    /**
     * Deletes all sport centers notifications in notificationList and in disk.
     */
    public void removeNotificationList(){
        notificationList.clear();

    }
    public boolean isFilled(){
        return filled;
    }
    public void setNotificationList(List<SportCenterNotification> l){
        this.notificationList=l;
    }
}
