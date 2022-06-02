package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

//import com.example.consumerapp.Model.Tools.Action;
//import com.example.consumerapp.Model.FireBaseDataBase.Firebase_DBManager_User;
//import com.example.consumerapp.Model.Entities.User;
//import com.example.consumerapp.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
//
public class MapsFragment extends FragmentActivity implements
        Serializable,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,    //to the that the connection succesful
        GoogleApiClient.OnConnectionFailedListener,    //when the connection is faild
        LocationListener    //when the location changed
{
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentUserLocationMarker;
    private static final int Request_User_Location_Code =99;
    Button signup_btn;
    public   static  EditText addressField;
//    public static User user;
    ProgressBar progressBar;
    int sum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);

        mAuth=FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        //קבלת מפה בצורה אסיכנרונית על ידיי פונקציה מובנית של המחלקה SupportMapFragment
        //משתמשים בפונקציה א-סינכרונית כדי לא להכביד על האפליקציה כלומר על ה-thread הראשי וככה
        //נשאיר את האפליקציה רספונסיבית
        mapFragment.getMapAsync(this);
        //בדיקת הרשאות מיקום של המשתמש
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkUserLocationPermission();
        }

    }

//    public void onClick(View view)
//    {
//        addressField=findViewById(R.id.enter_address);
//        String address=addressField.getText().toString();
//        List<Address> addressList = null;
//        MarkerOptions userMarkerOptions = new MarkerOptions();
//
//        if(!TextUtils.isEmpty(address))
//        {
//            Geocoder geocoder = new Geocoder(this);
//            try
//            {
//                addressList=geocoder.getFromLocationName(address,1);
//
//                if(addressList != null && !addressList.isEmpty())
//                {
//                    currentUserLocationMarker.remove();
//                    for (int i=0; i<addressList.size(); i++)
//                    {
//                        Address userAddress = addressList.get(i);
//                        LatLng latLng = new LatLng(userAddress.getLatitude(),userAddress.getLongitude());
//                        userMarkerOptions.position(latLng);
//                        userMarkerOptions.title(address);
//                        userMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//                        mMap.addMarker(userMarkerOptions);
//                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//                        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
//                    }
//                    signup_btn.setEnabled(true);
//                    user.setAddress(address);
//                }
//                else
//                {
//                    Snackbar.make(findViewById(android.R.id.content), "הכתובת לא נמצאה", Snackbar.LENGTH_LONG).show();
//                    signup_btn.setEnabled(false);
//                    user.setAddress("");
//                }
//            }
//
//            catch (IOException e)
//            {
//                e.printStackTrace();
//                signup_btn.setEnabled(false);
//                Snackbar.make(findViewById(android.R.id.content), "הכתובת לא נמצאה", Snackbar.LENGTH_LONG).show();
//            }
//
//            //when the api return number of results, the user will pick one...
//            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                @Override
//                public boolean onMarkerClick(Marker marker)
//                {
//                    addressField.setText(marker.getTitle());
//                    return false;
//                }
//            });
//        }
//        else
//        {
//            Snackbar.make(findViewById(android.R.id.content), "נא הכנס כתובת", Snackbar.LENGTH_LONG).show();
//        }
//    }

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
    //בונה API לגוגל ומגידרה את המיקום להיות ב-ISRAEL
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //הרשאת גישה ל-GPS תקינה
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in israel and move the camera
        LatLng sydney = new LatLng(32.017136, 34.745441);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Israel"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
    //פונקציה המשתמשת ב-synchronized כדי לא ליצור מצב ששתי תהליכים שונים יצרו את אותה מפה
//API ייחודי
    protected synchronized void buildGoogleApiClient()
    {
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }


    //בדיקת הרשאות גישה של האפליקציה טרום לחיצה בפלאפון
    public boolean checkUserLocationPermission()
    {
        //ברירת מחדל שהמשתמש הגדיר בפלאפון-אין הרשאות מיקום לאלפיקציות מיקום
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            //בא ננסה לבקש הרשאה מהמשתמש
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION))
                //Request_User_Location_Code-קיימות שלש אופציות למשתמש איזה סוג גישה הוא מסכים
                //החלון של האופציות קפץ והמשתמש בחר אחת מהאופציות
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            else
                //החלון של האופציות לא קפץ או שוהמשתמש  לא בחר שום אופציה
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, Request_User_Location_Code);
            return false;
        }

        else return true;

    }

    @Override
    //פונקציה  לבידקת הרשאות גישה בפלאפון
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Request_User_Location_Code:
                //בודק שבאמת המשתמש נתן הרשאות גחשה
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (googleApiClient == null)
                            buildGoogleApiClient();
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Snackbar.make(findViewById(android.R.id.content), "Permission Denied...", Snackbar.LENGTH_LONG).show();
                }
                return;
        }
    }

    @Override
    //פונקציה לשינוי כתובת במידה והמשתמש הזיז את ה-MARKER
    public void onLocationChanged(Location location)
    {
        lastLocation=location;
//התנתקות ממיקום קודם כדיי להתחבר למיקום חדש
        if(currentUserLocationMarker != null)   //the location connect to another location
        {
            currentUserLocationMarker.remove();
        }
//קבלת קורדינטות מיקום החדש אליו הוזזנו את ה-Marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        List<Address> addressList = null;

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
//אחראי על הפיכת הקורדינטות לכתובת ספציפית
//        Geocoder geocoder = new Geocoder(this);
//        if(Geocoder.isPresent()) {
////            final EditText addressField = findViewById(R.id.enter_address);
//            try {
//                //מכניסה את הכתובת שהוחזרה לי מהקורדינטות לתוך הרשימה
//                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);     //move the current address to the list
//                //לקיחת מיקום יחידי מתוך כל הכתובת
////                String address = (addressList.get(0).getAddressLine(0) + " " + addressList.get(0).getAddressLine(1) + " " + addressList.get(0).getAddressLine(2)).replaceAll("null", "");
////                addressField.setText(address);
////                user.setAddress(address);
////                signup_btn.setEnabled(true);
//            } catch (IOException e) {
//                Snackbar.make(findViewById(android.R.id.content), "לא ניתן למצוא את מיקומך הנוכחי", Snackbar.LENGTH_LONG).show();
////                signup_btn.setEnabled(false);
////                user.setAddress("");
//            }

            //מיקום המצלמה במבט על , על המיקום החדש אליו המשתמש הזיז את ה-marker
            markerOptions.title("מיקומך הנוכחי");
            mMap.getCameraPosition();
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            currentUserLocationMarker = mMap.addMarker(markerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.animateCamera(CameraUpdateFactory.zoomBy(12));


//        }

    }

    @Override
    //this func call whenever the device connected
    public void onConnected(@Nullable Bundle bundle)
    {
        locationRequest=new LocationRequest();
        locationRequest.setInterval(2000);  //milisecond
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    //this func call whenever the connection is failed...
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {

        //write messege to user
    }


}
