package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;

public class Group extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button createGroup;
    Button buttonLogout;
    Button group1;

    private RecyclerView groups;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelGroup> groupsList;
    private AdapterGroupsList adapterGroupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        createGroup = findViewById(R.id.button_create_group);
//        buttonLogout = findViewById(R.id.button_logout);
        group1 = findViewById(R.id.group1);
        groups = findViewById(R.id.groups);
        firebaseAuth = FirebaseAuth.getInstance();
        loadGroups();

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

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CreateGroup.class));
            }
        });

        group1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), GroupProfile.class));
            }
        });
    }

    private void loadGroups(){
        groupsList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupsList.size();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //groupsList.add(ds.getValue(ModelGroup.class));
                    groupsList.add(new ModelGroup(ds.child("Name").getValue().toString(),
                            ds.child("numOfFriends").getValue().toString(),
                            ds.child("sizeOfDogs").getValue().toString(),
                            ds.child("Description").getValue().toString(),
                            null));
                }
               // groupsList.add(new ModelGroup("erin", "2",
                 //       "walksPerWeek", "vnicn;", null));
                adapterGroupsList = new AdapterGroupsList(Group.this, groupsList);
                groups.setAdapter(adapterGroupsList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}
             