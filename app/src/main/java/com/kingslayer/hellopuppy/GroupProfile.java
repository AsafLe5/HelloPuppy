package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class GroupProfile extends AppCompatActivity {
    private RecyclerView users;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelUser> usersList;
    private AdapterUserList adapterUserList;
    private BottomNavigationView bottomNavigationView;
    private String groupId;
    private List<String> membersArray;
    private Map<String,ModelUser> usersModelMap = new HashMap<String,ModelUser>();;
    private boolean modUser = true;
    private boolean modDog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }
        usersList = null;
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        users = findViewById(R.id.users);
        firebaseAuth = FirebaseAuth.getInstance();
        loadUsers();

        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(), Group.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        return true;
                    case R.id.schedule:
                        startActivity(new Intent(getApplicationContext(), Schedule.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.chat:
                        startActivity(new Intent(getApplicationContext(), Chat.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.find_dog:
                        startActivity(new Intent(getApplicationContext(), FindDog.class));
                        overridePendingTransition(0, 0);
                        return true;
                }

                return false;
            }
        });
        //endregion
    }

    private void loadUsers() {
        usersList = new ArrayList<>();


        DatabaseReference membersIdsDR = FirebaseDatabase.getInstance().getReference().child("Groups")
                .child(groupId).child("MembersIds");
        DatabaseReference usersDR = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference dogsDR = FirebaseDatabase.getInstance().getReference("Dogs");


        membersIdsDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.size();
                long c = snapshot.getChildrenCount();
                int a = 4;
                membersArray = (List<String>) snapshot.getValue();

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        usersDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (String userIdString : membersArray) {
                    for (DataSnapshot attribute : snapshot.child(userIdString).getChildren()) {
                        switch (attribute.getKey().toString()) {
                            case "Full name":
                                if (modDog != true)
                                    usersModelMap.put(userIdString, new ModelUser());
                                usersModelMap.get(userIdString).setUserName(attribute.getValue().toString());
                               // UsersModelMap[attribute.getKey()] = new ModelUser();
                                //newUserModel.setUserName(attribute.getValue().toString());
                                break;

/*
                            case "Full":

                                break;
*/
                            default:
                        }
                    }
                }
                modUser = true;
                if (modDog == true)
                    updateMod();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        dogsDR.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (String userIdString : membersArray) {
                    for (DataSnapshot dogAttribute : snapshot.child(userIdString).getChildren()) {
                        switch (dogAttribute.getKey().toString()) {
                            case "Name":
                                if (modUser != true)
                                    usersModelMap.put(userIdString, new ModelUser());
                                ModelUser asd = usersModelMap.get(userIdString);
                                asd.setDogsName(dogAttribute.getValue().toString());
                                break;
/*
                            case "Full":

                                break;
*/
                            default:
                        }
                        //String a = attribute.getValue().toString();
                        System.out.println("dfg");
                    }
                    //snapshot.child(user).getValue();
                }
                modDog = true;
                if (modUser == true)
                    updateMod();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }
    void updateMod(){
        for (Map.Entry<String, ModelUser> model : usersModelMap.entrySet()){
            usersList.add(model.getValue());
        }
        adapterUserList = new AdapterUserList(GroupProfile.this, usersList);
        users.setAdapter(adapterUserList);
    }
}

