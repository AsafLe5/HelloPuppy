package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kingslayer.hellopuppy.Adapters.AdapterJoinRequests;
import com.kingslayer.hellopuppy.Models.ModelUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinRequests extends AppCompatActivity {
    private String groupId;
    private List<String> requestsIds;
    private Map<String, ModelUser> usersModelMap = new HashMap<String, ModelUser>();
    private ArrayList<ModelUser> usersList;
    private AdapterJoinRequests adapterUserList;
    private RecyclerView requests;
    private List<String> usersInGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join_requests);
        requests = findViewById(R.id.requests);
        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }
        loadJoinRequests();
        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();
        DbRef.child("Users").child("Tempi").setValue("deleteInAMinute");

    }

    private void loadJoinRequests() {

        usersList = new ArrayList<>();

        DatabaseReference DbRef = FirebaseDatabase.getInstance().getReference();

        DbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child("Groups").child(groupId).hasChild("Join requests")) {
                    requestsIds = (List<String>) snapshot.child("Groups").child(groupId)
                            .child("Join requests").getValue();
                    usersInGroup = (List<String>) snapshot.child("Groups").child(groupId)
                            .child("MembersIds").getValue();
                    assert requestsIds != null;
                    for (String memberId : requestsIds) {
                        if (memberId.equals("mask"))
                            continue;
//                    snapshot.child("Users").child(member);
                        ModelUser currentUser = new ModelUser();
                        currentUser.setUserName(snapshot.child("Users").child(memberId)
                                .child("Full name").getValue().toString());
                        currentUser.setAvailability(snapshot.child("Users").child(memberId)
                                .child("Availability").getValue().toString());
                        currentUser.setUserId(memberId);
                        usersModelMap.put(memberId, currentUser);

                    }
                    updateRequestMod();
                }
                else {
                    Toast.makeText(JoinRequests.this, "You don't have new requests", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


    }

    void updateRequestMod(){
        for (Map.Entry<String, ModelUser> model : usersModelMap.entrySet()){
            usersList.add(model.getValue());
        }
        adapterUserList = new AdapterJoinRequests(JoinRequests.this, usersList, groupId,usersInGroup, requestsIds);
        requests.setAdapter(adapterUserList);
    }
}