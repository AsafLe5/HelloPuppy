package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private EditText nameOfGroup;
    private EditText descriptionOfGroup;
    private Spinner numOfFriends;
    private Spinner sizeOfDogs;
    private Spinner walksPerWeek;
    private Spinner requireAvailability;
    private Button createGroup;
    private Map<String, String> choices = new HashMap<String,String>(4);
    private String groupId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_group);

        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        nameOfGroup = findViewById(R.id.name_of_group);
        descriptionOfGroup = findViewById(R.id.group_description);
        createGroup = findViewById(R.id.create_group);

        getSupportActionBar().setTitle("Create group");

        //region $ Spinner and Adapters initialization
        numOfFriends = (Spinner) findViewById(R.id.limit_friends_spinner);
        ArrayAdapter<CharSequence> numOfFriendsAdapter = ArrayAdapter.
                createFromResource(this,R.array.one_to_seven, android.R.layout.simple_spinner_item);
        numOfFriendsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numOfFriends.setAdapter(numOfFriendsAdapter);
        numOfFriends.setOnItemSelectedListener(this);

        sizeOfDogs = findViewById(R.id.size_of_dogs_spinner);
        ArrayAdapter<CharSequence> sizeOfDogsAdapter = ArrayAdapter.
                createFromResource(this,R.array.size_of_dog, android.R.layout.simple_spinner_item);
        sizeOfDogsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sizeOfDogs.setAdapter(sizeOfDogsAdapter);
        sizeOfDogs.setOnItemSelectedListener(this);

        walksPerWeek = findViewById(R.id.walks_per_week_spinner);
        ArrayAdapter<CharSequence> walksPerWeekAdapter = ArrayAdapter.
                createFromResource(this,R.array.one_to_seven, android.R.layout.simple_spinner_item);
        walksPerWeekAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        walksPerWeek.setAdapter(walksPerWeekAdapter);
        walksPerWeek.setOnItemSelectedListener(this);

        requireAvailability = findViewById(R.id.require_availability_spinner);
        ArrayAdapter<CharSequence> requireAvailabilityAdapter = ArrayAdapter.
                createFromResource(this,R.array.availabilities, android.R.layout.simple_spinner_item);
        requireAvailabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requireAvailability.setAdapter(requireAvailabilityAdapter);
        requireAvailability.setOnItemSelectedListener(this);


        //region $ Group's listener


        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                groupId = ""+System.currentTimeMillis();

                DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups")
                        .child(groupId);

                // name of group
                groupRef.child("Name").setValue(nameOfGroup.getText().toString());

                // description of the group
                groupRef.child("Description").setValue(descriptionOfGroup.getText().toString());

                /**** upload photo**/

                // all spinners of group
                for (Map.Entry<String,String> chs : choices.entrySet()){
                    groupRef.child(chs.getKey()).setValue(chs.getValue());
                }


                //String[] names = {FirebaseAuth.getInstance().getUid().toString()};
                List<String> nameList;
                nameList = new ArrayList<>();
                nameList.add(FirebaseAuth.getInstance().getUid().toString());
//                groupRef.child("MembersIds").setValue(nameList);
                groupRef.child("MembersIds").setValue(nameList).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "shalom", Toast.LENGTH_LONG).show();

                    }
                });


                // storing my group id
                FirebaseDatabase.getInstance().getReference().child("Users")
                        .child(FirebaseAuth.getInstance().getUid().toString())
                        .child("GroupId").setValue(groupId);

                groupRef.child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Credits").setValue(5);

                groupRef.child("FindDog").child("CurrentlyOnTrip").setValue("");
//                groupRef.child("groupManagerId").setValue(FirebaseAuth.getInstance().getUid().toString());

                groupRef.child("groupManagerId").setValue(FirebaseAuth.getInstance().getUid())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
//                        Toast.makeText(getApplicationContext(), "shalom", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), Group.class));
                        overridePendingTransition(0,0);
                    }
                });

                groupRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
//                        if(snapshot.hasChild("Name") && snapshot.hasChild("Description")
//                                &&snapshot.hasChild("MembersIds") && snapshot.hasChild("ScheduleChoices")
//                                &&snapshot.hasChild("FindDog") && snapshot.hasChild("groupManagerId")) {

                        if (snapshot.getChildrenCount() == 10){
                            Toast.makeText(CreateGroup.this, "Group created successfully"
                                    , Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), Group.class));
                            overridePendingTransition(0,0);
//                        }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
            }
        });
        //endregion

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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        switch (adapterView.getId()){
            case R.id.limit_friends_spinner:
                choices.put("numOfFriends", choice);
                break;
            case R.id.size_of_dogs_spinner:
                choices.put("sizeOfDogs", choice);
                break;
            case R.id.walks_per_week_spinner:
                choices.put("walksPerWeek", choice);
                break;
            case R.id.require_availability_spinner:
                choices.put("requireAvailability", choice);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}