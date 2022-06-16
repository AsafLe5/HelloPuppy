package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingslayer.hellopuppy.Adapters.AdapterRemoveUser;
import com.kingslayer.hellopuppy.Adapters.AdapterUserList;
import com.kingslayer.hellopuppy.Models.ModelUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManageGroup extends AppCompatActivity {
    private RecyclerView users;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelUser> usersList;
    private AdapterRemoveUser adapterRemoveUserList;
    private BottomNavigationView bottomNavigationView;
    private String groupId;
    private List<String> membersArray;
    private Map<String,ModelUser> usersModelMap = new HashMap<String,ModelUser>();;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_group);

        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }

        getSupportActionBar().setTitle("Manage group");

        usersList = null;
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        users = findViewById(R.id.users);
        firebaseAuth = FirebaseAuth.getInstance();
        loadUsers();

        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.group:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
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

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();

        DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//
//                // only manager sees join requests button
//                String manager = snapshot.child("Groups").child(groupId).child("groupManagerId").getValue().toString();
//                if(manager.equals(FirebaseAuth.getInstance().getUid().toString())){
//                    isManager = true;
////                    buttonJoinRequests.setVisibility(View.VISIBLE);
////                    buttonJoinRequests.setEnabled(true);
//                }

                membersArray = (List<String>) snapshot.child("Groups").child(groupId)
                        .child("MembersIds").getValue();
                for(String member: membersArray){
                    ModelUser asd = new ModelUser(member);
                    asd.setUserName(snapshot.child("Users").child(member)
                            .child("Full name").getValue().toString());

                    asd.setDogsName(snapshot.child("Dogs").child(member)
                            .child("Name").getValue().toString());

//                    asd.setUserId(member);
                    usersModelMap.put(member, asd);

                }
                updateMod();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DbRef.child("Users").child("Tempi").setValue("deleteInAMinute");
        DbRef.child("Users").child("Tempi").removeValue();
    }

    void updateMod(){
        for (Map.Entry<String, ModelUser> model : usersModelMap.entrySet()){
            usersList.add(model.getValue());
        }
        adapterRemoveUserList = new AdapterRemoveUser(ManageGroup.this, usersList);
        users.setAdapter(adapterRemoveUserList);
    }

}

