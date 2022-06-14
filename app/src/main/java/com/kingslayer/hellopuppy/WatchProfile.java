package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

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
    private TextView dogGenderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_profile);

        getSupportActionBar().setTitle("User profile");

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);

        userPhoto = findViewById(R.id.imageView);
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
        dogGenderText = findViewById(R.id.dog_gender_text);

        if (getIntent().hasExtra("User")){
            user = getIntent().getStringExtra("User");
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

        DatabaseReference dbUserRef = FirebaseDatabase.getInstance().getReference();

        dbUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                DataSnapshot dbUser = snapshot.child("Users").child(user);
                DataSnapshot dbDog = snapshot.child("Dogs").child(user);

                if (dbUser.hasChild("Full name")) {
                    nameText.setText(dbUser.child("Full name").getValue().toString());
                }

                if (dbUser.hasChild("Birth Day")) {
                    ageText.setText(dbUser.child("Birth Day").getValue().toString());
                }

                if (dbUser.hasChild("Location")) {
                    locationText.setText(dbUser.child("Location").getValue().toString());
                }

                if (dbUser.hasChild("Gender")) {
                    genderText.setText(dbUser.child("Gender").getValue().toString());
                }

                if (dbUser.hasChild("Availability")) {
                    availabilityText.setText(dbUser.child("Availability").getValue().toString());
                }

                if (dbUser.hasChild("Profile photo")) {
                    Picasso.get().load(dbUser.child("Profile photo").getValue().toString()).into(userPhoto);
                }

                if (dbDog.hasChild("Dog Birth Day")) {
                    dogAgeText.setText(dbDog.child("Dog Birth Day").getValue().toString());
                }

                if (dbDog.hasChild("Name")) {
                    dogNameText.setText(dbDog.child("Name").getValue().toString());
                }

                if (dbDog.hasChild("Age")) {
                    dogAgeText.setText(dbDog.child("Age").getValue().toString());
                }

                if (dbDog.hasChild("Gender")) {
                    dogGenderText.setText(dbDog.child("Gender").getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}