package com.example.remindat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public  class MapActivity extends AppCompatActivity {

    private FusedLocationProviderClient client;
    private SupportMapFragment mapFragment;
    private int REQUEST_CODE=111;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private GoogleMap gMap;
    private Geocoder geocoder;
    private double selectedLat,selectedLng;
    private List<Address> addresses;
    private String selectedAddress;
    private double startLatitude;
    private double startLongitude;
    private double destLatitude;
    private double destLongitude;

    private EditText mSearchAdrress;
    private ImageButton mSearchBtn;
    private Button mSelDest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getSupportActionBar().hide();
        mSearchAdrress = (EditText)findViewById(R.id.search_bar);
        mSearchBtn = (ImageButton)findViewById(R.id.search_btn);
        mSelDest = (Button)findViewById(R.id.sel_dest);
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        client = LocationServices.getFusedLocationProviderClient(MapActivity.this);


        if (ActivityCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            init();
            getCurrentLocation();

        }
        else
        {
            ActivityCompat.requestPermissions(MapActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
    }


    private void init()
    {


        mSearchBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               geoLocate();
           }
       });
    }




    private void geoLocate()
    {
        String searchStr = mSearchAdrress.getText().toString();
        Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address>  list = new ArrayList<>();
        try{

            list = geocoder.getFromLocationName(searchStr,1);
            if(list.size()>0)
            {
                Address address = list.get(0);
//                Toast.makeText(this,address.toString(), Toast.LENGTH_SHORT).show();

                moveCamera(new LatLng(address.getLatitude(),address.getLongitude()));
            }
            else
            {
                Toast.makeText(this, "No results found", Toast.LENGTH_SHORT).show();
            }

        }
        catch (IOException e)
        {
            Toast.makeText(this, "Some Error Occurred", Toast.LENGTH_SHORT).show();
        }

    }



    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions

            return;
        }
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {

                            gMap = googleMap;
                            LatLng latLng= new LatLng(location.getLatitude(),location.getLongitude());
                            startLatitude = location.getLatitude();
                            startLongitude = location.getLongitude();
                            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You are here");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
                            googleMap.addMarker(markerOptions ).showInfoWindow();

                            gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                @Override
                                public void onMapClick(@NonNull LatLng latLng) {
                                    if(CheckConnection())
                                    {
                                        selectedLat = latLng.latitude;
                                        selectedLng = latLng.longitude;

                                        destLatitude = selectedLat;
                                        destLongitude = selectedLng;

                                        GetAddress(selectedLat,selectedLng);

                                    }
                                    else
                                    {
                                        Toast.makeText(MapActivity.this, "Check your connection", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }
                    });
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                getCurrentLocation();
        }
        else
        {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

    }


    private void moveCamera(LatLng latLng)
    {//search bar
        gMap.clear();

        LatLng latLngSrc= new LatLng(startLatitude,startLongitude);
        MarkerOptions markerOptions = new MarkerOptions().position(latLngSrc).title("You");
        gMap.addMarker(markerOptions).showInfoWindow();



         markerOptions = new MarkerOptions().position(latLng).title("Destination");
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
        gMap.addMarker(markerOptions).showInfoWindow();


        mSelDest.setOnClickListener(new View.OnClickListener() {
            String temp = latLng.latitude +" " +latLng.longitude;
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("lat_long_Value", temp);
                setResult(RESULT_OK, intent);
                finish();
//                finishActivity(100);

            }
        });

        double dist = getDistanceFromLatLon(startLatitude,startLongitude,latLng.latitude, latLng.longitude);
        String unit = " m";
        if(dist >= 1000)
        {
            dist = dist/1000;
            unit = " km";
        }

        Toast.makeText(this, String.format("%.1f",dist) +unit, Toast.LENGTH_SHORT).show();

    }

    private int flag = 0;

    private boolean CheckConnection()
    {
        connectivityManager = (ConnectivityManager)getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(networkInfo.isConnected() && networkInfo.isAvailable())
            return true;
        else
            return false;
    }

    private void GetAddress(double lat, double lng)
    {
        geocoder = new Geocoder(MapActivity.this, Locale.getDefault());

        if(lat!=0)
        {
            try {
                addresses = geocoder.getFromLocation(lat,lng,1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(addresses!=null)
            {
                String address = addresses.get(0).getAddressLine(0);

                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
                String dis = addresses.get(0).getSubAdminArea();

                selectedAddress = address;

                if(address!=null)
                {
                    gMap.clear();

                    LatLng latLng= new LatLng(startLatitude,startLongitude);
                    MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("You");
                    gMap.addMarker(markerOptions).showInfoWindow();


                    markerOptions = new MarkerOptions();
                    //Direct

                    latLng = new LatLng(lat,lng);
                    markerOptions.position(latLng).title(selectedAddress);
                    gMap.addMarker(markerOptions).showInfoWindow();

                    mSelDest.setOnClickListener(new View.OnClickListener() {
                        String temp = lat +" " +lng;
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent();
                            intent.putExtra("lat_long_Value", temp);
                            setResult(RESULT_OK, intent);
                            finish();
//                finishActivity(100);

                        }
                    });


                    double dist = getDistanceFromLatLon(startLatitude,startLongitude,destLatitude,destLongitude);
                    String unit = " m";
                    if(dist >= 1000)
                    {
                        dist = dist/1000;
                        unit = " km";
                    }

                    Toast.makeText(this, String.format("%.1f",dist) +unit, Toast.LENGTH_SHORT).show();



                }
                else
                {
                    Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "LatLng Null", Toast.LENGTH_SHORT).show();
        }

    }

    private double  getDistanceFromLatLon(double lat1,double lon1,double lat2,double lon2) {
        double R = 6378100; // Radius of the earth in m
        double dLat = deg2rad(lat2-lat1);
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in m
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }


}