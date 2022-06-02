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
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.support.v4.app.ActivityCompat;
//import android.app.FragmentActivity;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindDog extends FragmentActivity implements OnMapReadyCallback{

    private GoogleMap mMap;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private final long MIN_TIME = 1000; // 1 second
    private final long MIN_DIST = 5; // 5 Meters

    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_dog_2);
// Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PackageManager.PERMISSION_GRANTED);

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

        // Add a marker in Sydney and move the camera
        LatLng ramatGan = new LatLng(32.0673, 34.8388);
        mMap.addMarker(new MarkerOptions().position(ramatGan).title("Marker at Bar-Ilan"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(ramatGan));

        float zoomLevel = 16.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ramatGan, zoomLevel));

        Geocoder geocoder = new Geocoder(FindDog.this.getApplicationContext(), Locale.getDefault());


        try {
            List<Address> addresses;
            addresses = geocoder.getFromLocation(ramatGan.latitude, ramatGan.longitude, 1);

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("My Position"));

//                    Geocoder geocoder;
                    List<Address> addresses;
                    Geocoder geocoder = new Geocoder(FindDog.this.getApplicationContext(), Locale.getDefault());

                    try {
                        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                        String city = addresses.get(0).getLocality();
//                        String state = addresses.get(0).getAdminArea();
//                        String country = addresses.get(0).getCountryName();
//                        String postalCode = addresses.get(0).getPostalCode();
//                        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }
    }
}