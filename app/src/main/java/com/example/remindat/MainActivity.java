package com.example.remindat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SetData {
    private static final String TAG = "Tag";
    FloatingActionButton floatingActionButton;
    RecyclerView recyclerView;
    ArrayList<Model> dataList;
    Adapter ad1;
    Sqlite sq;

    FusedLocationProviderClient client2;
    double[] user_latlong = new double[2];

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomFlag bottomLayout = new BottomFlag(ad1, dataList, MainActivity.this);
                bottomLayout.show(getSupportFragmentManager(), "Tag");


            }

        });
        dataList = new ArrayList<>();
        ad1 = new Adapter(getApplicationContext(), dataList, this);

        recyclerView.setAdapter(ad1);
        sq = new Sqlite(getApplicationContext());
        Cursor c1 = sq.getalldata();
        if (c1.getCount() == 0) {
            Log.e(TAG, "onCreate: " + "nodata");
        } else {
            while (c1.moveToNext()) {
                Model m = new Model(c1.getString(1), c1.getInt(2), c1.getInt(0), c1.getDouble(3), c1.getDouble(4));
                dataList.add(m);
            }
            ad1.notifyDataSetChanged();

        }


        final Handler handler = new Handler();
        final int delay = 5000; // 1000 milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {

                //Fetch user location every 5 second
                getCurrentLocation() ;


                //Keep checking with saved latitude and longitude

                Cursor cursor = sq.getalldata();
                while (cursor.moveToNext())
                {
                    Model m = new Model(cursor.getString(1), cursor.getInt(2), cursor.getInt(0), cursor.getDouble(3), cursor.getDouble(4));
//                    Toast.makeText(MainActivity.this, m.lat+" "+m.lon, Toast.LENGTH_SHORT).show();
                    if(m.status !=1 && validLatLong(m.lat, m.lon) && getDistanceFromLatLon(user_latlong[0],user_latlong[1],m.lat,m.lon) <= 75)//75 metre
                    {
                        Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                        intent.putExtra("arg", m.Task);
                        startActivity(intent);
//                        delete(m.id);
                        update(m.id, 1);
                    }
                }


//                Toast.makeText(MainActivity.this, user_latlong[0]+" "+user_latlong[1], Toast.LENGTH_SHORT).show();

                handler.postDelayed(this, delay);
            }
        }, delay);


    }

    public void delete(int id) {

        sq.deleteddata(id);
        dataList.clear();
        Log.e(TAG, "delete: " + dataList.size());
        Cursor c1 = sq.getalldata();
        if (c1.getCount() == 0) {
            Log.e(TAG, "onCreate: " + "nodata");
            dataList.clear();

        } else {
            while (c1.moveToNext()) {
                Model m = new Model(c1.getString(1), c1.getInt(2), c1.getInt(0), c1.getDouble(3), c1.getDouble(4));

                dataList.add(m);
            }

        }
        ad1.notifyDataSetChanged();
    }

    public void update(int id, int status) {
        Log.e(TAG, "update: " + id + status);
        this.sq.updatedata(id, status);
        dataList.clear();
        Cursor c1 = sq.getalldata();
        if (c1.getCount() == 0) {
            Log.e(TAG, "onCreate: " + "nodata");
            dataList.clear();
           } else {
            while (c1.moveToNext()) {
                Model m = new Model(c1.getString(1), c1.getInt(2), c1.getInt(0), c1.getDouble(3), c1.getDouble(4));
                dataList.add(m);
            }

        }
        ad1.notifyDataSetChanged();


    }


    public void getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Toast.makeText(this, "FFFFFFF", Toast.LENGTH_SHORT).show();
            Log.e(TAG,"Permission error");
            return ;
        }
        client2 = LocationServices.getFusedLocationProviderClient(this);

        Task<Location> task = client2.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!=null)
                {
                    user_latlong[0] = location.getLatitude();
                    user_latlong[1] = location.getLongitude();

//                    Toast.makeText(MainActivity.this, latlong[0]+" "+latlong[1], Toast.LENGTH_SHORT).show();

                }

            }
        });


    }


    public boolean validLatLong(double lat, double lon)
    {
        return lat != 0 || lon != 0 || user_latlong[0] != 0 || user_latlong[1] != 0;
    }



    public double  getDistanceFromLatLon(double lat1,double lon1,double lat2,double lon2) {
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

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

}