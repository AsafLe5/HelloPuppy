package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.sql.SQLOutput;
import java.util.Objects;

public class Profile extends AppCompatActivity implements EditNameDialog.EditNameDialogListener {
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


    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        intent = getIntent();
        Bundle extras = intent.getExtras();
        String profileNameString  = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        profileImageUri = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        dogsName = findViewById(R.id.dogs_name);
        System.out.println("het");

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
        Picasso.get().load(profileImageUri).into(profileImage);



        try {
            profileImage.setImageDrawable(Drawable.createFromStream(
                    getContentResolver().openInputStream(profileImageUri),null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        buttonEditName =findViewById(R.id.buttonEditName);
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's name", "dogs_name");
            }
        });

        buttonEditGender = findViewById(R.id.buttonEditGender);
        buttonEditGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your gender", "your_gender");
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

        buttonEditAvailability =findViewById(R.id.buttonEditAvailability);
        buttonEditAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter number of days you are available in a week",
                        "availability");
            }
        });


        buttonEditDogsGender =findViewById(R.id.buttonEditGenderD);
        buttonEditDogsGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your dog's gender",
                        "dogs_gender");
            }
        });

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

//    public void applyText(String name, String str) {
//        TextView t = findViewById(R.id.str);
//        t.setText(name);
//    }

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
                TextView t4 = findViewById(R.id.your_gender);
                t4.setText(newText);
                //addToUserFB("Gender", newText);
                break;

            case("location"): /** TODO: handle with GPS later*/
                TextView t5 = findViewById(R.id.location);
                t5.setText(newText);
                break;

            case("availability"): //listBox
                TextView t6 = findViewById(R.id.availability);
                t6.setText(newText);
                break;

            case("dogs_gender"): //listBox - male,female
                TextView t7 = findViewById(R.id.dogs_gender);
                t7.setText(newText);
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
}