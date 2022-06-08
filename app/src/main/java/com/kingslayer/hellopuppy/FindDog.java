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

import android.content.Intent;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;


public class FindDog extends AppCompatActivity{
    private String myGroupId;
    private String currentlyOnTrip;
    private String nameOfWalker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference fb = FirebaseDatabase.getInstance().getReference();


        fb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.child("Users").child(FirebaseAuth.getInstance().getUid().toString())
                        .hasChild("GroupId")) {

                    myGroupId = snapshot.child("Users").child(FirebaseAuth.getInstance().getUid().toString())
                            .child("GroupId").getValue().toString();

                    currentlyOnTrip = snapshot.child("Groups").child(myGroupId).child("FindDog")
                            .child("CurrentlyOnTrip").getValue().toString();

                    if(!currentlyOnTrip.equals(""))
                        nameOfWalker = snapshot.child("Users").child(currentlyOnTrip)
                                .child("Full name").getValue().toString();

                    startPage();

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public void startPage(){
//        if(currentlyOnTrip.equals(FirebaseAuth.getInstance().getUid().toString())){
//            // find what to do
//        }

        if(currentlyOnTrip.equals("")){
            FirebaseDatabase.getInstance().getReference("Groups").child(myGroupId)
                    .child("FindDog").child("CurrentlyOnTrip").setValue(FirebaseAuth.getInstance().getUid().toString());

            Intent intent = new Intent(getApplicationContext(), FindDogOne.class);
            intent.putExtra("myGroupId", myGroupId);
            startActivity(intent);

        }

        else{
            Intent intent = new Intent(getApplicationContext(), FindDogOthers.class);
            intent.putExtra("name", nameOfWalker);
            intent.putExtra("myGroupId", myGroupId);
            startActivity(intent);
        }
    }
}