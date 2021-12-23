package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

//import com.github.drjacky.imagepicker.ImagePicker;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class Profile extends AppCompatActivity implements EditNameDialog.EditNameDialogListener {
    private TextView nameTextView;
    private Button buttonEditName;
    private String profileImageStr;
    private Uri profileImageUri;
    private ImageView profileImage;
    private TextView dogsName;
    FloatingActionButton addProfileImage;


    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //profileImageUrl = getIntent().getExtras().get("ProfileImage").toString();
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //profileImageUrl = intent.getExtras().toString();

        profileImageStr = extras.getString("ProfileImage");
        String profileNameString  = extras.getString("ProfileName");
        profileImageUri = Uri.parse(profileImageStr);
        //profileImageUri = Uri.parse("/drawable/ic_google_login.png");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        dogsName = findViewById(R.id.dogs_name);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(), Group.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
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
        //profileImage = findViewById(R.id.ProfileImage);
        addProfileImage = findViewById(R.id.addProfileImage);
        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(Profile.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .cropOval()	    		//Allow dimmed layer to have a circle inside
                        .compress(1024)	    //Let the user to resize crop bounds
                        .maxResultSize(1080,1080)
                        .start();
            }
        });

        nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(profileNameString);
        profileImage = findViewById(R.id.profileImage);
        Picasso.get().load(profileImageUri).into(profileImage);

//        profileImage.setImageURI(null);
//        profileImage.setImageURI(profileImageUri);
        try {
            profileImage.setImageDrawable(Drawable.createFromStream(
                    getContentResolver().openInputStream(profileImageUri),null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
//        profileImage.setImageBitmap(bi);
//        Bitmap bitmap = BitmapFactory.decodeStream(profileImageUri);
        //profileImage = (ImageView) findViewById(R.id.profileImage);
        //profileImage.setImageBitmap(BitmapFactory.decodeFile(profileImageUri));

        buttonEditName =findViewById(R.id.buttonEditName);
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog();
            }
        });
    }
    public void openEditNameDialog(){
        EditNameDialog editNameDialog = new EditNameDialog("Enter dog's name");
        editNameDialog.show(getSupportFragmentManager(),null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Uri uri = data.getData();
        //profileImage.setImageURI(uri);
    }

    @Override
    public void applyText(String name) {
        dogsName.setText(name);
    }
}