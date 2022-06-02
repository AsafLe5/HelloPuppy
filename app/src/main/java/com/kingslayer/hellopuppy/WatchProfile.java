package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class WatchProfile extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private String user;
    private ImageView userPhoto;
    private TextView nameText;
    private TextView genderText;
    private TextView ageText;
    private TextView locationText;
    private TextView availabilityText;
    private ImageView dogPhoto;
    private TextView dogNameText;
    private TextView dogAgeText;
    private TextView isVaccinatedText;
    private TextView isCastratedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        nameText = findViewById(R.id.name_text);
        genderText = findViewById(R.id.user_gender_text);
        ageText = findViewById(R.id.age_text);
        locationText = findViewById(R.id.location_text);
        availabilityText = findViewById(R.id.availability_text);
        //dogPhoto = findViewById(R.id.);
        dogNameText = findViewById(R.id.dog_name_text);
        dogAgeText = findViewById(R.id.dog_age_text);
        isVaccinatedText = findViewById(R.id.is_vaccinated_text);
        isCastratedText = findViewById(R.id.is_castrated_text);
        if (getIntent().hasExtra("User")){
            user = getIntent().getParcelableExtra("User").toString();
        }

        setUserProfile();


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
    }

    private void setUserProfile() {

        DatabaseReference dbUserRef = FirebaseDatabase.getInstance()
                .getReference().child("Users").child(user);
        dbUserRef.child("Age").get(); /// lo batuah sheyaavod/\...
        // ve az laasot kaha lekulam!!!!!!!!1

    }
}