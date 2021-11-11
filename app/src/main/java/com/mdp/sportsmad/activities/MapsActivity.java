package com.mdp.sportsmad.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import com.mdp.sportsmad.R;
import com.mdp.sportsmad.databinding.ActivityMapsBinding;
import com.mdp.sportsmad.model.SportCenter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private SportCenter sportCenter;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedlocationProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Get sportCenter
        Intent i = getIntent();
        Gson gson = new Gson();
        sportCenter = gson.fromJson(getIntent().getStringExtra("sportCenter"), SportCenter.class);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //Location of user
        fusedlocationProvider = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(100);
        locationRequest.setSmallestDisplacement(0);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                //remove any existing marker
                //mMap.clear();
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(position).title("Your position"));
                    //mMap.moveCamera(CameraUpdateFactory.zoomTo(17));//Zoom
                    //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(position);
                    //mMap.moveCamera(cameraUpdate);//Position
                    //Stop retrieving positions. We only want one.
                    fusedlocationProvider.removeLocationUpdates(locationCallback);
                }
            }
        };
        //Load user location
        if(!startLocationCallBack()){
            //if permission not yet granted:
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng = sportCenter.getLatLng();

        mMap.addMarker(new MarkerOptions().position(latLng).title(sportCenter.getTitle()));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    /**
     * Start the load of the user location
     * If it the permission is granted, then returns true, else false and a dialog to request the permission is needed
     * @return
     */
    private boolean startLocationCallBack(){
        if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedlocationProvider.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            return true;
        }else
            return false;
    }
    /**
     * Register the permissions callback, which handles the user's response to the
     * system permissions dialog. Save the return value, an instance of
     * ActivityResultLauncher, as an instance variable.
     */
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    startLocationCallBack();
                } else {
                    // The execution is still valid but the location of the user will not be added to the map
                    Toast.makeText(this,"Your location will not be added to the map",Toast.LENGTH_SHORT).show();
                }
            });
}