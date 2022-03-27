package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class Profile extends AppCompatActivity implements EditNameDialog.EditNameDialogListener, AdapterView.OnItemSelectedListener {
    private TextView nameTextView;
    private Button buttonEditName;
    private Button buttonEditGender;
    private Button buttonEditAge;
    private Button buttonEditDogsAge;
    private Button buttonEditDogsGender;
    private Button buttonEditDogsBreed;
    private Button buttonEditLocation;
    private Button buttonEditAvailability;
    private String profileImageStr;
    private Uri profileImageUri;
    private ImageView profileImage;
    private TextView dogsName;
    FloatingActionButton addProfileImage;
    private Intent intent;
    private Spinner userGenderSpinner;
    private Spinner availabilitySpinner;
    private Spinner dogGenderSpinner;

    BottomNavigationView bottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //intent = getIntent();
        //Bundle extras = intent.getExtras();
        String profileNameString  = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        profileImageUri = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        dogsName = findViewById(R.id.dogs_name);

        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(), Group.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
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
                        startActivity(new Intent(getApplicationContext(),FindDog.class));
                        overridePendingTransition(0,0);
                        return true;
                }

                return false;
            }
        });
        //endregion

        addProfileImage = findViewById(R.id.addProfileImage);
        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(Profile.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .cropOval()	    		//Allow dimmed layer to have a circle inside
                        .compress(1024)	    //Let the user to resize crop bounds
                        .maxResultSize(1080,1080)
                        .start();
//                intent.putExtra("NewProfileImage", Objects.requireNonNull(user.getPhotoUrl()).toString());
            }
        });
        nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(profileNameString);
        profileImage = findViewById(R.id.profileImage);


        if(isFacebookUser()){
            String facebookUserId = "";
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // find the Facebook profile and get the user's id
            for(UserInfo profile : user.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }

            // construct the URL to the profile picture, with a custom height
            // alternatively, use '?type=small|medium|large' instead of ?height=
            String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

            // (optional) use Picasso to download and show to image
            Picasso.get().load(photoUrl).into(profileImage);
        }
        else {
            Picasso.get().load(profileImageUri).into(profileImage);
        }

        buttonEditName =findViewById(R.id.buttonEditName);
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's name", "dogs_name");
            }
        });

        buttonEditAge =findViewById(R.id.buttonEditAge);
        buttonEditAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your age", "your_age");
            }
        });

        buttonEditDogsAge = findViewById(R.id.buttonEditDogsAge);
        buttonEditDogsAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's age", "dogs_age");
            }
        });

        buttonEditLocation =findViewById(R.id.buttonEditLocation);
        buttonEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your location", "location");
            }
        });

        userGenderSpinner = findViewById(R.id.your_gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.
                createFromResource(this,R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGenderSpinner.setAdapter(genderAdapter);
        userGenderSpinner.setOnItemSelectedListener(this);

        availabilitySpinner =findViewById(R.id.availability);
        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.
                createFromResource(this,R.array.availabilities, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        availabilitySpinner.setOnItemSelectedListener(this);

        dogGenderSpinner =findViewById(R.id.dogs_gender);
        ArrayAdapter<CharSequence> dogsGenderAdapter = ArrayAdapter.
                createFromResource(this,R.array.genders, android.R.layout.simple_spinner_item);
        dogsGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogGenderSpinner.setAdapter(dogsGenderAdapter);
        dogGenderSpinner.setOnItemSelectedListener(this);

        buttonEditDogsBreed =findViewById(R.id.buttonEditBreed);
        buttonEditDogsBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your dog's breed",
                        "dogs_breed");
            }
        });
    }
    public void openEditNameDialog(String hint, String strViewToChange){
        EditNameDialog editNameDialog = new EditNameDialog(hint, strViewToChange);
        editNameDialog.show(getSupportFragmentManager(),null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(CHANGE_PROFILE_CODE == 1){
//            String newProfileImage= data.getStringExtra("result");
//            Uri newProfileImage= (Uri) data.getExtras().get("profilePicture");

//            Uri uri  = Uri.parse(newProfileImage);
//            Picasso.get().load(newProfileImage).into(profileImage);
//            Bundle extras = intent.getExtras();
//            String newProfileImage = extras.getString("NewProfileImage");
//            Uri uri  = Uri.parse(newProfileImage);
//            Picasso.get().load(uri).into(profileImage);
//        }


    }

    public void goToApplyText(String newText, String textViewToApply){
        switch (textViewToApply){
            case ("dogs_name"):
                TextView t = findViewById(R.id.dogs_name);
                t.setText(newText);
                addToDogFB("Name", newText);
                break;

            case("your_age"):
                TextView t2 = findViewById(R.id.your_age);
                t2.setText(newText);
                addToUserFB("Age", newText);
                break;

            case("dogs_age"):
                TextView t3 = findViewById(R.id.dogs_age);
                t3.setText(newText);
                break;

            case("your_gender"):
                //TextView t4 = findViewById(R.id.your_gender);
                //t4.setText(newText);
                //addToUserFB("Gender", newText);
                break;

            case("location"): /** TODO: handle with GPS later*/
                TextView t5 = findViewById(R.id.location);
                t5.setText(newText);
                break;
        }
    }

    public void addToUserFB(String attribute, String newText){
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid().toString())
                .child(attribute).setValue(newText);
    }

    public void addToDogFB(String attribute, String newText){
        FirebaseDatabase.getInstance().getReference().child("Dogs")
                .child(FirebaseAuth.getInstance().getUid().toString())
                .child(attribute).setValue(newText);
    }

    boolean isFacebookUser(){
        for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        switch (adapterView.getId()){
            case R.id.availability:
                addToUserFB("Availability", choice);
                break;
            case R.id.dogs_gender:
                addToDogFB("Gender", choice);
                break;
            case R.id.your_gender:
                addToUserFB("Gender", choice);
                break;
            default:
                break;
        }
        //Toast.makeText(getApplicationContext(),adapterView.getId(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}