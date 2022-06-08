//package com.kingslayer.hellopuppy;
//
//import android.Manifest;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.pm.PackageManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//
//public class FindDog extends AppCompatActivity implements OnMapReadyCallback {
//
//}
////    SupportMapFragment mapFragment;
////    GoogleMap mMap;
////    Marker marker;
////    LocationBroadcastReceiver receiver;
////
////    @Override
////    protected void onCreate(@Nullable Bundle savedInstanceState) {
////        super.onCreate(savedInstanceState);
////        setContentView(R.layout.activity_find_dog);
////        receiver = new LocationBroadcastReceiver();
////        if (Build.VERSION.SDK_INT >= 23) {
////            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
////            } else {
////                //Req Location Permission
////                startLocService();
////            }
////        } else {
////            //Start the Location Service
////            startLocService();
////        }
////        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
////        mapFragment.getMapAsync(this);
////    }
////
////    void startLocService() {
////        IntentFilter filter = new IntentFilter("ACT_LOC");
////        registerReceiver(receiver, filter);
////        Intent intent = new Intent(FindDog.this, LocationService.class);
////        startService(intent);
////    }
////
////    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        switch (requestCode) {
////            case 1:
////                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
////                    //startLocService();
////                } else {
////                    Toast.makeText(this, "Give me permissions", Toast.LENGTH_LONG).show();
////                }
////        }
////    }
////
////    @Override
////    public void onMapReady(GoogleMap googleMap) {
////        mMap = googleMap;
////    }
////
////    @Override
////    protected void onPause() {
////        super.onPause();
////        unregisterReceiver(receiver);
////    }
////
////    public class LocationBroadcastReceiver extends BroadcastReceiver {
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            if (intent.getAction().equals("ACT_LOC")) {
////                double lat = intent.getDoubleExtra("latitude", 0f);
////                double longitude = intent.getDoubleExtra("longitude", 0f);
////                if (mMap != null) {
////                    LatLng latLng = new LatLng(lat, longitude);
////                    MarkerOptions markerOptions = new MarkerOptions();
////                    markerOptions.position(latLng);
////                    if (marker != null)
////                        marker.setPosition(latLng);
////                    else
////                        marker = mMap.addMarker(markerOptions);
////                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
////                }
////                Toast.makeText(FindDog.this, "Latitude is: " + lat + ", Longitude is " + longitude, Toast.LENGTH_LONG).show();
////            }
////        }
////    }
////}

package com.kingslayer.hellopuppy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FindDogOne extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private Button startBtn;
    private Button endBtn;

    private final long MIN_TIME = 1000; // 1 second
    private final long MIN_DIST = 5; // 5 Meters

    private LatLng latLng;
    private String myGroupId;

    private ExtendedFloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;

    private Timer timer;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        myGroupId = intent.getStringExtra("myGroupId");
        setContentView(R.layout.activity_find_dog_one);
        startBtn = findViewById(R.id.start_button);
        endBtn = findViewById(R.id.end_button);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start map
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(FindDogOne.this);

                ActivityCompat.requestPermissions(FindDogOne.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PackageManager.PERMISSION_GRANTED);
                ActivityCompat.requestPermissions(FindDogOne.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PackageManager.PERMISSION_GRANTED);

                mLocationClient = new FusedLocationProviderClient(FindDogOne.this);


                startTimer();


                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCurrLocation();
                    }
                });

                fab.callOnClick();

                // disable start button
                startBtn.setEnabled(false);

                //enable end button
                endBtn.setEnabled(true);
            }
        });

        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // disable end trip button
                endBtn.setEnabled(false);

                // save that the trip is over
                FirebaseDatabase.getInstance().getReference("Groups").child(myGroupId)
                        .child("FindDog").child("CurrentlyOnTrip").setValue("");

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getCurrLocation();
                    }
                });
                fab.callOnClick();

                Intent intent1 = new Intent(getApplicationContext(), FindDog.class);
                startActivity(intent1);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void getCurrLocation() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();

                // save location at DB
                FirebaseDatabase.getInstance().getReference("Groups").child(myGroupId)
                        .child("FindDog").child("location").child("latitude").setValue(location.getLatitude());
                FirebaseDatabase.getInstance().getReference("Groups").child(myGroupId)
                        .child("FindDog").child("location").child("longitude").setValue(location.getLongitude());

                goToLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng ltlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, 16.0f);
        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(FindDogOne.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FindDogOne.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }

        else{
            return;
        }
    }


    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        timer.schedule(timerTask, 0, 5000);
    }

    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        //code to run after every 5 seconds

                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCurrLocation();
                            }
                        });

                        fab.callOnClick();
                    }
                });
            }
        };
    }

    @Override
    public void onConnected(@Nullable @org.jetbrains.annotations.Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }
}