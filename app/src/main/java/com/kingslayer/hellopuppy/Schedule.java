package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Schedule extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Button chooseShifts;
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

//        Intent intent = getIntent();
//        groupId = intent.getStringExtra("GroupId");

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.schedule);
        chooseShifts = findViewById(R.id.choose_shifts);

        chooseShifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // trigger onDataChange
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
//                reference.child("Tempi").setValue("deleteInAMinute");
//                reference.child("Tempi").removeValue();
                if(groupId != null){
                    Intent intent = new Intent(getApplicationContext(), ChooseShifts.class);
                    intent.putExtra("GroupId", groupId);
                    startActivity(intent);
                }
            }
        });

        String myId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){

                    assert myId != null;
                    if(ds.getKey().equals(myId) && ds.hasChild("GroupId")){
                        groupId = ds.child("GroupId").getValue().toString();
                    }
                }
//
////                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
////                setContentView(R.layout.activity_chat);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


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
                        startActivity(new Intent(getApplicationContext(), Profile.class));
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


        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(
                MakeShifts.class, 7, TimeUnit.DAYS).build();
        WorkManager.getInstance().enqueue(periodicWorkRequest);
    }
}