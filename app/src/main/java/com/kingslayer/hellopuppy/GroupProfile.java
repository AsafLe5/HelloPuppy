package com.kingslayer.hellopuppy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingslayer.hellopuppy.Adapters.AdapterUserList;
import com.kingslayer.hellopuppy.Models.ModelUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupProfile extends AppCompatActivity {
    private RecyclerView users;
    private RecyclerView requestUsers;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelUser> usersList;
    private AdapterUserList adapterUserList;
    private BottomNavigationView bottomNavigationView;
    private Button buttonJoinRequests;
    private String groupId;
    private List<String> membersArray;
    private Map<String,ModelUser> usersModelMap = new HashMap<String,ModelUser>();;
    private boolean modUser = true;
    private boolean modDog = false;
    private boolean isManager = false;
    private TextView explanation;
    private TextView nameOfGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);
        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }

        getSupportActionBar().setTitle("Group profile");

//        buttonJoinRequests = findViewById(R.id.button_join_requests);
//        buttonJoinRequests.setVisibility(View.INVISIBLE);
//        buttonJoinRequests.setEnabled(false);

        usersList = null;
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        users = findViewById(R.id.users);
        explanation = findViewById(R.id.explanation_text);
        firebaseAuth = FirebaseAuth.getInstance();
        nameOfGroup = findViewById(R.id.name_of_group);
        loadUsers();
//
//        buttonJoinRequests.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), JoinRequests.class);
//                intent.putExtra("GroupId", groupId);
//                startActivity(intent);
//                overridePendingTransition(0,0);
////                startActivity(new Intent(getApplicationContext(), Profile.class));
////                overridePendingTransition(0, 0);
//            }
//        });

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

                // set the description of the group
                if(snapshot.child("Groups").child(groupId).hasChild("Description")){
                    explanation.setText(snapshot.child("Groups").child(groupId).child("Description")
                            .getValue().toString());
                }

                if(snapshot.child("Groups").child(groupId).hasChild("Name")){
                    nameOfGroup.setText(snapshot.child("Groups").child(groupId).child("Name")
                            .getValue().toString());
                }

                // only manager sees join requests button
                String manager = snapshot.child("Groups").child(groupId).child("groupManagerId").getValue().toString();
                if(manager.equals(FirebaseAuth.getInstance().getUid().toString())){
                    isManager = true;
//                    buttonJoinRequests.setVisibility(View.VISIBLE);
//                    buttonJoinRequests.setEnabled(true);
                }

                membersArray = (List<String>) snapshot.child("Groups").child(groupId)
                        .child("MembersIds").getValue();
                for(String member: membersArray){
                    ModelUser asd = new ModelUser(member);
                    asd.setUserName(snapshot.child("Users").child(member)
                            .child("Full name").getValue().toString());

                    asd.setDogsName(snapshot.child("Dogs").child(member)
                            .child("Name").getValue().toString());
                    if(snapshot.child("Users").child(member).hasChild("Profile photo")){
                        asd.setUserProfile(snapshot.child("Users").child(member)
                                .child("Profile photo").getValue().toString());
                    }

                    usersModelMap.put(member, asd);

                }
                updateMod();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        DbRef.child("Users").child("Tempi").setValue("deleteInAMinute");
//        DbRef.child("Users").child("Tempi").removeValue();
    }
    void updateMod(){
        for (Map.Entry<String, ModelUser> model : usersModelMap.entrySet()){
            usersList.add(model.getValue());
        }
        adapterUserList = new AdapterUserList(GroupProfile.this, usersList);
        users.setAdapter(adapterUserList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isManager)
            getMenuInflater().inflate(R.menu.menu_group_profile, menu);
        else
            getMenuInflater().inflate(R.menu.menu_group_profile_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (!isManager){
            int id = item.getItemId();
            switch (id){
                case R.id.leave_group:
                    checkAndLeave();
                    break;
            }
        }
        else{
            int id = item.getItemId();
            switch (id){
                case R.id.requests:
                    Intent intent = new Intent(getApplicationContext(), JoinRequests.class);
                    intent.putExtra("GroupId", groupId);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    break;
                case R.id.manage_group:
                    Intent intent2 = new Intent(getApplicationContext(), ManageGroup.class);
                    intent2.putExtra("GroupId", groupId);
                    startActivity(intent2);
                    overridePendingTransition(0,0);
                    break;

                case R.id.delete_group:
                    checkAndDelete();
                    break;
            }
        }
        return true;
    }

    public void checkAndDelete(){
        new AlertDialog.Builder(GroupProfile.this)
                .setTitle("Pay attention!")
                .setMessage("Are you sure you want to delete this group?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteGroup();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();

    }

    public void checkAndLeave(){
        new AlertDialog.Builder(GroupProfile.this)
                .setTitle("Pay attention!")
                .setMessage("Are you sure you want to leave this group?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        leaveGroup();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public void deleteGroup(){
        // remove group id for every member in the group
        for(String member: membersArray){
            FirebaseDatabase.getInstance().getReference("Users").child(member)
                    .child("GroupId").removeValue();
        }

        // save that the trip is over
        FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                .child("FindDog").child("CurrentlyOnTrip").setValue("");

        //delete the group
        FirebaseDatabase.getInstance().getReference("Groups").child(groupId).removeValue();

        Intent intent = new Intent(getApplicationContext(), Group.class);
        startActivity(intent);
    }

    public void leaveGroup(){
        boolean isMeCurrOnTrip = false;
        // remove me from MembersIds of the group
        if(membersArray.contains(FirebaseAuth.getInstance().getUid().toString())){
            membersArray.remove(FirebaseAuth.getInstance().getUid().toString());
        }
        FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                .child("MembersIds").setValue(membersArray);

        // remove my schedule
        FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                .child("ScheduleChoices").child(FirebaseAuth.getInstance()
                                .getUid().toString()).removeValue();

        // delete my group id
        FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance()
                .getUid().toString()).child("GroupId").removeValue();

        Intent intent = new Intent(getApplicationContext(), Group.class);
        startActivity(intent);
    }
}

