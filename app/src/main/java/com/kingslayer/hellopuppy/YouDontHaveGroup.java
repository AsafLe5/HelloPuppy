package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class YouDontHaveGroup extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private String fromActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle B = getIntent().getExtras();
        fromActivity = B.getString("fromActivity");

        setContentView(R.layout.activity_you_dont_have_group);
        getSupportActionBar().setTitle(fromActivity);


        if(fromActivity.equals("Chat")){
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.chat);

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

        if(fromActivity.equals("FindDog")){
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.find_dog);

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

        if(fromActivity.equals("Schedule")){
            bottomNavigationView = findViewById(R.id.bottom_navigator);
            bottomNavigationView.setSelectedItemId(R.id.schedule);

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
    }

}