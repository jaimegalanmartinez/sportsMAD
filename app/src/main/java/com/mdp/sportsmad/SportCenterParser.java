package com.mdp.sportsmad;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.mdp.sportsmad.model.SportCenter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SportCenterParser {
    private Context context;
    private  List<SportCenter> list;
    public  SportCenterParser(Context context){
        this.context = context;
    }
    public void parse(String text) {
        List<SportCenter> list = new ArrayList<>();
        try {
            JSONObject mainObject = new JSONObject(text);//loadJSONFromAsset(file)
            JSONArray graphs = ( JSONArray) mainObject.get("@graph");
            for(int index =0; index<graphs.length();index++){
                JSONObject graph = graphs.getJSONObject(index);
                int id=Integer.parseInt(graph.getString("id"));
                String title=(String) graph.get("title");
                String type="Gimnasios";
                if(graph.has("@type")) {
                    String[] typeSplit = graph.getString("@type").split("/");
                    type = typeSplit[typeSplit.length - 1];
                }
                String urlRelation=graph.getString("relation");
                String street="";
                if(graph.getJSONObject("address").has("street-address"))
                    street = graph.getJSONObject("address").getString("street-address");
                //if(graph.getJSONObject("location").has("street-address"))
                LatLng latLng = new LatLng(graph.getJSONObject("location").getDouble("latitude"),graph.getJSONObject("location").getDouble("longitude"));
                String schedule = graph.getJSONObject("organization").getString("schedule");
                String services = graph.getJSONObject("organization").getString("services");
                list.add(new SportCenter(id,title,type,urlRelation,street,latLng,schedule,services));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.list=list;
    }
    public List<com.mdp.sportsmad.model.SportCenter> getParse(){
        return this.list;
    }
    private String loadJSONFromAsset(String file) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch ( IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
