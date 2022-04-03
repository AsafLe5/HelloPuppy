package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class GroupProfile extends AppCompatActivity {
    private RecyclerView users;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModelUser> usersList;
    private AdapterUserList adapterUserList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_profile);

        users = findViewById(R.id.users);
        firebaseAuth = FirebaseAuth.getInstance();
        loadUsers();

    }

    private void loadUsers(){
        usersList = new ArrayList<>();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                usersList.size();
                for(DataSnapshot ds: snapshot.getChildren()){
                    usersList.add(ds.getValue(ModelUser.class));
                }
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                usersList.add(new ModelUser("yossi", "yossi_junior"));
                adapterUserList = new AdapterUserList(GroupProfile.this, usersList);
                users.setAdapter(adapterUserList);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}