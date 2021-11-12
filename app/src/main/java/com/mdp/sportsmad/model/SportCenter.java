package com.mdp.sportsmad.model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Class describing the object of a Sport Center
 */
public class SportCenter implements Serializable {
    private int id;
    private String title;
    private String type;
    private String urlRelation;
    private String street ;
    private LatLng latLng;
    private String schedule;
    private String services;

    public SportCenter(int id,String title,String type,String urlRelation,String street,LatLng latLng,String schedule,String services){
        this.id= id;
        this.title= title;
        this.type=type;
        this.urlRelation=urlRelation;
        this.street=street;
        this.latLng=latLng;
        this.schedule=schedule;
        this.services=services;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrlRelation() {
        return urlRelation;
    }

    public void setUrlRelation(String urlRelation) {
        this.urlRelation = urlRelation;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }
}
