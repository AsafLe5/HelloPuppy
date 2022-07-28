package com.kingslayer.hellopuppy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class FindDogOne extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {

    private BottomNavigationView bottomNavigationView;
    private GoogleMap mMap;
    private Button startBtn;
    private String myGroupId;
    private ExtendedFloatingActionButton fab;
    private FusedLocationProviderClient mLocationClient;
    private boolean startButton = true;
    private TimerTask timerTask;
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        myGroupId = intent.getStringExtra(Constants.MY_GROUP_ID);
        setContentView(R.layout.activity_find_dog_one);
        startBtn = findViewById(R.id.start_button);
        fab = findViewById(R.id.fab);
        fab.setVisibility(View.INVISIBLE);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.find_dog);
        getSupportActionBar().setTitle(Constants.FIND_DOG_TITLE);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start map
                // Obtain the SupportMapFragment and get notified when the map is ready to be used.

                if (startButton) {
                    FirebaseDatabase.getInstance().getReference(Constants.GROUPS_DB).child(myGroupId)
                            .child(Constants.FIND_DOG_DB).child(Constants.CURRENTLY_ON_TRIP_DB)
                            .setValue(FirebaseAuth.getInstance().getUid().toString());

                    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(FindDogOne.this);

                    ActivityCompat.requestPermissions(FindDogOne.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PackageManager.PERMISSION_GRANTED);
                    ActivityCompat.requestPermissions(FindDogOne.this,
                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
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
                    startBtn.setText(Constants.END_TRIP_TEXT);
                    startButton = false;
                }
                else {
                    // save that the trip is over
                    FirebaseDatabase.getInstance().getReference(Constants.GROUPS_DB).child(myGroupId)
                            .child(Constants.FIND_DOG_DB).child(Constants.CURRENTLY_ON_TRIP_DB).setValue("");

                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getCurrLocation();
                        }
                    });
                    fab.callOnClick();

                    Intent intent1 = new Intent(getApplicationContext(), FindDog.class);
                    startActivity(intent1);

                    startBtn.setText(Constants.START_TRIP_TEXT);
                    startButton = true;
                }
            }
        });

        if (getIntent().hasExtra(Constants.IS_ON_TRIP)) {
            startBtn.performClick();
        }


        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(),Group.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.schedule:
                        startActivity(new Intent(getApplicationContext(), Schedule.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(),Chat.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.find_dog:
                        return true;
                }
                return false;
            }
        });
        //endregion

    }

    @SuppressLint("MissingPermission")
    private void getCurrLocation() {
        mLocationClient.getLastLocation().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Location location = task.getResult();

                // save location at DB
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_DB).child(myGroupId)
                        .child(Constants.FIND_DOG_DB).child(Constants.LOCATION_DB).child(Constants.LATITUDE_DB)
                        .setValue(location.getLatitude());
                FirebaseDatabase.getInstance().getReference(Constants.GROUPS_DB).child(myGroupId)
                        .child(Constants.FIND_DOG_DB).child(Constants.LOCATION_DB).child(Constants.LONGITUDE_DB)
                        .setValue(location.getLongitude());

                goToLocation(location.getLatitude(), location.getLongitude());
            }
        });
    }

    private void goToLocation(double latitude, double longitude) {
        LatLng ltlng = new LatLng(latitude, longitude);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(ltlng, Constants.ZOOM_LEVEL);
        mMap.moveCamera(cameraUpdate);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(FindDogOne.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(FindDogOne.this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
        }
    }


    public void startTimer() {
        //set a new Timer
        Timer timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();
        timer.schedule(timerTask, 0, Constants.FIVE_SECONDS);
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