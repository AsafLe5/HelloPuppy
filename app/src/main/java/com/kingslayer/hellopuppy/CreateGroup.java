package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private EditText nameOfGroup;
    private EditText descriptionOfGroup;
    private Button uploadGroupImage;
    private Uri imageUri;
    private Spinner numOfFriends;
    private Spinner sizeOfDogs;
    private Spinner walksPerWeek;
    private Spinner requireAvailability;
    private Button createGroup;
    private Map<String, String> choices = new HashMap<String,String>(4);
    private static final int IMAGE_REQUEST = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.group);
        nameOfGroup = findViewById(R.id.name_of_group);
        descriptionOfGroup = findViewById(R.id.group_description);
        createGroup = findViewById(R.id.create_group);
        uploadGroupImage = findViewById(R.id.button_upload_image);

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

        //endregion

        //region $ Group's listener

        uploadGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImage();
                //uploadImage();
            }
        });

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // name of group
                FirebaseDatabase.getInstance().getReference().child("Groups")
                        .child(FirebaseAuth.getInstance().getUid().toString())
                        .child("Name").setValue(nameOfGroup.getText().toString());

                // description of the group
                FirebaseDatabase.getInstance().getReference().child("Groups")
                        .child(FirebaseAuth.getInstance().getUid().toString())
                        .child("Description").setValue(descriptionOfGroup.getText().toString());

                // upload photo

                // all spinners of group
                for (Map.Entry<String,String> chs : choices.entrySet()){
                    FirebaseDatabase.getInstance().getReference().child("Groups")
                            .child(FirebaseAuth.getInstance().getUid().toString())
                            .child(chs.getKey()).setValue(chs.getValue());
                }

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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            imageUri = data.getData();
            //uploadImage();
        }
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri !=  null){
            StorageReference fileRef = FirebaseStorage.getInstance().getReference()
                    .child("Group profile photos")
                    .child(FirebaseAuth.getInstance().getUid().toString() + "." + getFileExtention(imageUri));
            fileRef.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();

                            Log.d("DownloadUrl", url);
                            pd.dismiss();
                            Toast.makeText(CreateGroup.this, "Image upload successfull"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

    }
    private String getFileExtention(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
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