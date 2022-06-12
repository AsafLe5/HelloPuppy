package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Chat extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_page);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.chat);

        String myId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){

                    assert myId != null;
                    if(ds.getKey().equals(myId) && ds.hasChild("GroupId")){
                        String groupId = ds.child("GroupId").getValue().toString();
                        Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                        intent.putExtra("GroupId", groupId);
                        findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                        startActivity(intent);
                        overridePendingTransition(0,0);
                        return;
                    }
                }
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                setContentView(R.layout.activity_chat);
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
}