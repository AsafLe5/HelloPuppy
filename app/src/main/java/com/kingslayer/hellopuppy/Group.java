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
import com.kingslayer.hellopuppy.Adapters.AdapterGroupsList;
import com.kingslayer.hellopuppy.Models.ModelGroup;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Group extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    Button createGroup;
    Button buttonLogout;
    Button group1;
    private String myGroupId;
    private RecyclerView groups;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelGroup> groupsList = new ArrayList<>();
//    private ArrayList<ModelGroup> groupsList;
    private AdapterGroupsList adapterGroupsList;
    private DatabaseReference fb;
    private Button requsestJoinBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requsestJoinBtn = findViewById(R.id.RequestJoin);

        String myId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){

                    assert myId != null;
                    String sdf = ds.getKey();
                    if(ds.getKey().equals(myId) && ds.hasChild("GroupId")){
                        String groupId = ds.child("GroupId").getValue().toString();
                            Intent intent = new Intent(getApplicationContext(), GroupProfile.class);
                            intent.putExtra("GroupId", groupId);
                            startActivity(intent);
                            overridePendingTransition(0,0);
                            return;
                    }
                }
                initializeGroups();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // trigger onDataChange
        reference.child("Tempi").setValue("deleteInAMinute");
        reference.child("Tempi").removeValue();
    }

    private void initializeGroups() {
        setContentView(R.layout.activity_group);
        fb = FirebaseDatabase.getInstance().getReference();
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        createGroup = findViewById(R.id.button_create_group);



//        buttonLogout = findViewById(R.id.button_logout);

//        group1 = findViewById(R.id.group1);
        groups = findViewById(R.id.groups);
        firebaseAuth = FirebaseAuth.getInstance();
        loadGroups();


        DatabaseReference groupIdRef = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid().toString());


        groupIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild("GroupId")) {
                    myGroupId = snapshot.child("GroupId").getValue().toString();
//                    DatabaseReference membersIds = fb.child("Groups").child(myGroupId).child("MembersIds");
//                    membersIds.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot snapshot) {
//                            for (DataSnapshot postSnapshot: snapshot.getChildren()) {
//                                String s = snapshot.getValue().toString();
//                                System.out.println(snapshot.getValue()); // the String "John"
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                        }
//
//                    });
                }
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

//        group1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(getApplicationContext(), GroupProfile.class);
//                intent.putExtra("GroupId", myGroupId);
//                startActivity(intent);
//
//            }
//        });
    }

    private void loadGroups(){
//        groupsList = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                groupsList.size();
                groupsList = new ArrayList<>();

                for(DataSnapshot ds: snapshot.getChildren()){

                    ModelGroup newMG = new ModelGroup(ds.child("Name").getValue().toString(),
                            ds.child("numOfFriends").getValue().toString(),
                            ds.child("sizeOfDogs").getValue().toString(),
                            ds.child("Description").getValue().toString(),
                            null, ds.getKey().toString());
                    groupsList.add(newMG);

                }
                adapterGroupsList = new AdapterGroupsList(Group.this, groupsList);
                groups.setAdapter(adapterGroupsList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}