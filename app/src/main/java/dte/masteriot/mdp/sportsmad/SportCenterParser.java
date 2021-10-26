package dte.masteriot.mdp.sportsmad;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SportCenterParser {
    private String link="200186-0-polideportivos.json";
    private Context context;
    private  List<dte.masteriot.mdp.sportsmad.SportCenter> list;
    public  SportCenterParser(Context context){
        this.context = context;
    }
    public void parse(String text) {
        List<dte.masteriot.mdp.sportsmad.SportCenter> list  =new ArrayList<dte.masteriot.mdp.sportsmad.SportCenter>();
        try {
            JSONObject mainObject = new JSONObject(text);//loadJSONFromAsset(file)
            JSONArray graphs = ( JSONArray) mainObject.get("@graph");
            for(int index =0; index<graphs.length();index++){
                JSONObject graph = graphs.getJSONObject(index);
                int id=Integer.parseInt(graph.getString("id"));
                String title=(String) graph.get("title");
                String typeSplit[]=graph.getString("@type").split("/");
                String type = typeSplit[typeSplit.length-1];
                String urlRelation=graph.getString("relation");
                String street="";
                if(graph.getJSONObject("address").has("street-address"))
                    street = graph.getJSONObject("address").getString("street-address");
                //if(graph.getJSONObject("location").has("street-address"))
                LatLng latLng = new LatLng(graph.getJSONObject("location").getDouble("latitude"),graph.getJSONObject("location").getDouble("longitude"));
                String schedule = graph.getJSONObject("organization").getString("schedule");
                String services = graph.getJSONObject("organization").getString("services");
                list.add(new dte.masteriot.mdp.sportsmad.SportCenter(id,title,type,urlRelation,street,latLng,schedule,services));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.list=list;
    }
    public List<dte.masteriot.mdp.sportsmad.SportCenter> getParse(){
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
            json = new String(buffer, "UTF-8");
        } catch ( IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
