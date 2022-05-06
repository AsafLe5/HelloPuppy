package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GroupChatActivity extends AppCompatActivity {

    private String groupId;
    private Toolbar toolbar;
    private ImageView groupPic;
    private TextView groupTitle;
    private ImageButton attachBtn, sendBtn;
    private EditText messageEt;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        // init views
        toolbar = findViewById(R.id.toolbar);
        groupPic = findViewById(R.id.GroupPicture);
        groupTitle = findViewById(R.id.groupTitle);
        attachBtn = findViewById(R.id.attachBtn);
        messageEt = findViewById(R.id.messageEt);
        sendBtn = findViewById(R.id.sendBtn);

        // get id of the group
        Intent intent = getIntent();
        groupId = intent.getStringExtra("groupId");

        firebaseAuth = FirebaseAuth.getInstance();
        loadGroupInfo();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // input data
                String message = messageEt.getText().toString().trim();
                // check if the message is valid- not empty.
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(GroupChatActivity.this, "Can't send empty message",
                            Toast.LENGTH_SHORT).show();
                }

                else{
                    // send the message
                    sendMessage(message);
                }
            }
        });
    }

    private void sendMessage(String message) {
        // timestamp of the message
        String timeStamp = ""+System.currentTimeMillis();

        // setup message data
        HashMap<String, Object> map = new HashMap<>();
        map.put("sender", FirebaseAuth.getInstance().getUid());
        map.put("message", message);
        map.put("timestamp", timeStamp);
        map.put("type", "text"); // the type can be: text/picture/file.

        // add to the database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").child(timeStamp).setValue(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // message sent successfully


            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                // failed sending the message
                Toast.makeText(GroupChatActivity.this, e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });





    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByKey().equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String theGroupTitle = ds.child("Name").getValue().toString();
                    String theGroupDescription = ds.child("Description").getValue().toString();
                    groupTitle.setText(theGroupTitle);

//                    String theGroupPic = ds.child("").getValue().toString();
//                    try{
//                        Picasso.get().load(theGroupPic).placeholder(R.drawable.ic_profile).into(groupPic);
//
//                    } catch (Exception e){
//                        groupPic.setImageResource(R.drawable.ic_profile);
//                    }

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}