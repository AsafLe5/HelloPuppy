package com.kingslayer.hellopuppy;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class FindDogOthers extends FragmentActivity implements OnMapReadyCallback{
    private TextView name;
    private TextView address;

    private String currOnTrip;

    private GoogleMap mMap;
    private LatLng location;
    private String myGroupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        currOnTrip = intent.getStringExtra("name");
        myGroupId = intent.getStringExtra("myGroupId");

        setContentView(R.layout.find_dog_others);
        name = findViewById(R.id.name);
        name.setText(currOnTrip);

        address = findViewById(R.id.address);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        DatabaseReference locationDBR = FirebaseDatabase.getInstance().getReference("Groups")
                .child(myGroupId).child("FindDog").child("location");

        locationDBR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(mMap != null){
                    // Add a marker in Sydney and move the camera
                    location = new LatLng(Double.parseDouble(snapshot.child("latitude").getValue().toString()),
                            Double.parseDouble(snapshot.child("longitude").getValue().toString()));

                    mMap.addMarker(new MarkerOptions().position(location)
                            .title("Current location"));
                    float zoomLevel = 16.0f; //This goes up to 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

                    Locale aLocale = new Locale.Builder().setLanguage("en").setScript("Latn").setRegion("RS").build();

                    Geocoder geocoder = new Geocoder(FindDogOthers.this.getApplicationContext(),
                            aLocale);

                    try {
                        List<Address> addresses = geocoder.getFromLocation(location.latitude,
                                location.longitude, 1);
                        address.setText(addresses.get(0).getAddressLine(0));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
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

        if(location != null){
            mMap.addMarker(new MarkerOptions().position(location).title("Current location"));

            float zoomLevel = 16.0f; //This goes up to 21
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

            Geocoder geocoder = new Geocoder(FindDogOthers.this.getApplicationContext(), Locale.getDefault());

            try {
                List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
                address.setText(addresses.get(0).getAddressLine(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
