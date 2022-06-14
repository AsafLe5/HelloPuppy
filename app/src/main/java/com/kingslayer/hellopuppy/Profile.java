package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Objects;

public class Profile extends AppCompatActivity implements EditNameDialog.EditNameDialogListener, AdapterView.OnItemSelectedListener {
    private TextView nameTextView;
    private Button buttonEditName;
    private Button buttonEditGender;
//    private Button buttonEditAge;
    private Button buttonEditDogsAge;
    private Button buttonEditDogsGender;
    private Button buttonEditDogsBreed;
    private Button buttonEditLocation;
    private Button buttonEditAvailability;
    private String profileImageStr;
    private Uri profileImageUri;
    private ImageView profileImage;
    private TextView dogsName;
    FloatingActionButton addProfileImage;
    //    private Intent intent;
    private Spinner userGenderSpinner;
    private Spinner availabilitySpinner;
    private Spinner dogGenderSpinner;
//    private TextView usersAge;
    private TextView dogsAge;
    BottomNavigationView bottomNavigationView;
    View _rootView;
    private String userGender;
    private String availability;
    private String dogGender;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog dogDatePickerDialog;
    private Button dateButton;
    private Button dogDateButton;
    private boolean allFieldsGotFilled = false;
    private boolean hasGroup = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String profileNameString = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        profileImageUri = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl());

        initDatePicker();
        initDogDatePicker();
        dateButton = findViewById(R.id.date_picker_actions);
        //dateButton.setText(getTodaysDate());
        dogDateButton = findViewById(R.id.dog_date_picker);
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        getSupportActionBar().setTitle("Profile");

        dogsName = findViewById(R.id.dogs_name);
//        usersAge = findViewById(R.id.your_age);
        dogsAge = findViewById(R.id.dogs_age);

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                int numOfFilledFields = 0;
                DataSnapshot user = snapshot.child("Users").child(FirebaseAuth.getInstance().getUid().toString());
                DataSnapshot dog = snapshot.child("Dogs").child(FirebaseAuth.getInstance().getUid().toString());
                if (user.hasChild("Birth Day")) {
                    dateButton.setText(user.child("Birth Day").getValue().toString());
//                    usersAge.setText(user.child("Age").getValue().toString());
                    numOfFilledFields++;

                }
                if (dog.hasChild("Dog Birth Day")) {
                    dogDateButton.setText(dog.child("Dog Birth Day").getValue().toString());
//                    usersAge.setText(user.child("Age").getValue().toString());
                    numOfFilledFields++;

                }

//                if (dog.hasChild("Location")) {
//                    locat.setText(dog.child("Location").getValue().toString());
//                    numOfFilledFields++;
//                }


                if (dog.hasChild("Name")) {
                    dogsName.setText(dog.child("Name").getValue().toString());
                    numOfFilledFields++;
                }

                if (dog.hasChild("Age")) {
                    dogsAge.setText(dog.child("Age").getValue().toString());
                    numOfFilledFields++;

                }

                if (user.hasChild("Gender")) {
                    userGender = user.child("Gender").getValue().toString();
                    handleUserGender();
                    numOfFilledFields++;
                } else {
                    userGenderSpinner.setSelection(0);
                    addToUserFB("Gender", "male");
                }

                if (user.hasChild("Availability")) {
                    availability = user.child("Availability").getValue().toString();
                    handleAvailability();
                    numOfFilledFields++;
                } else {
                    availabilitySpinner.setSelection(0);
                    addToUserFB("Availability", "One day a week");
                }

                if (dog.hasChild("Gender")) {
                    dogGender = dog.child("Gender").getValue().toString();
                    handleDogGender();
                    numOfFilledFields++;
                } else {
                    dogGenderSpinner.setSelection(0);
                    addToDogFB("Gender", "male");
                }
                if (numOfFilledFields >= 7){
                    allFieldsGotFilled = true;
                }

                if(user.hasChild("GroupId")){
                    hasGroup = true;
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });


        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (allFieldsGotFilled) {
                    if(hasGroup){
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
                    }
                    else{
                        switch(item.getItemId()){
                            case R.id.profile:
                                return true;
                            case R.id.group:
                                startActivity(new Intent(getApplicationContext(), Group.class));
                                overridePendingTransition(0,0);
                                return true;
                            default:
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (!isFinishing()){
                                            new AlertDialog.Builder(Profile.this)
                                                    .setTitle("Warning!")
                                                    .setMessage("You Don't have a group yet!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            // Whatever...
                                                        }
                                                    }).show();
                                        }
                                    }
                                });
                        }
                    }
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (!isFinishing()){
                                new AlertDialog.Builder(Profile.this)
                                        .setTitle("Warning!")
                                        .setMessage("You Haven't filled all the fields yet!")
                                        .setCancelable(false)
                                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // Whatever...
                                            }
                                        }).show();
                            }
                        }
                    });
                }
                return false;
            }

        });
        //endregion

        addProfileImage = findViewById(R.id.addProfileImage);
        addProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(Profile.this)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .cropOval()                //Allow dimmed layer to have a circle inside
                        .compress(1024)        //Let the user to resize crop bounds
                        .maxResultSize(1080, 1080)
                        .start();
//                intent.putExtra("NewProfileImage", Objects.requireNonNull(user.getPhotoUrl()).toString());
            }
        });
        nameTextView = findViewById(R.id.nameTextView);
        nameTextView.setText(profileNameString);
        profileImage = findViewById(R.id.profileImage);


        if (isFacebookUser()) {
            String facebookUserId = "";
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // find the Facebook profile and get the user's id
            for (UserInfo profile : user.getProviderData()) {
                // check if the provider id matches "facebook.com"
                if (FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                    facebookUserId = profile.getUid();
                }
            }

            // construct the URL to the profile picture, with a custom height
            // alternatively, use '?type=small|medium|large' instead of ?height=
            String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";

            // (optional) use Picasso to download and show to image
            Picasso.get().load(photoUrl).into(profileImage);
        } else {
            Picasso.get().load(profileImageUri).into(profileImage);
            savePictureInDb(profileImageUri);
        }

        buttonEditName = findViewById(R.id.buttonEditName);
        buttonEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's name", "dogs_name");
            }
        });

//        buttonEditAge = findViewById(R.id.buttonEditAge);
//        buttonEditAge.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openEditNameDialog("Enter your age", "your_age");
//            }
//        });

        buttonEditDogsAge = findViewById(R.id.buttonEditDogsAge);
        buttonEditDogsAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's age", "dogs_age");
            }
        });

        buttonEditLocation = findViewById(R.id.buttonEditLocation);
        buttonEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your location", "location");
            }
        });

        userGenderSpinner = findViewById(R.id.your_gender);
        ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.
                createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGenderSpinner.setAdapter(genderAdapter);
        userGenderSpinner.setOnItemSelectedListener(this);
        handleUserGender();

        availabilitySpinner = findViewById(R.id.availability);
        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.
                createFromResource(this, R.array.availabilities, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        availabilitySpinner.setOnItemSelectedListener(this);
        handleAvailability();

        dogGenderSpinner = findViewById(R.id.dogs_gender);
        ArrayAdapter<CharSequence> dogsGenderAdapter = ArrayAdapter.
                createFromResource(this, R.array.genders, android.R.layout.simple_spinner_item);
        dogsGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogGenderSpinner.setAdapter(dogsGenderAdapter);
        dogGenderSpinner.setOnItemSelectedListener(this);
        handleDogGender();

        buttonEditDogsBreed = findViewById(R.id.buttonEditBreed);
        buttonEditDogsBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter your dog's breed",
                        "dogs_breed");
            }
        });
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                dateButton.setText(date);
                addToUserFB("Birth Day", date);

            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

    }

    private void initDogDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = makeDateString(day, month, year);
                dogDateButton.setText(date);
                addToDogFB("Dog Birth Day", date);

            }
        };
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int style = AlertDialog.THEME_HOLO_LIGHT;

        dogDatePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        dogDatePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    void savePictureInDb(Uri profileImageUri){

        String myId = FirebaseAuth.getInstance().getUid().toString();
        FirebaseDatabase.getInstance().getReference("Users").child(myId)
                .child("Profile photo").setValue(profileImageUri.toString());
    }

    private String makeDateString(int day, int month, int year) {
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month) {
        if (month == 1)
            return "JAN";
        if (month == 2)
            return "FEB";
        if (month == 3)
            return "MAR";
        if (month == 4)
            return "APR";
        if (month == 5)
            return "MAY";
        if (month == 6)
            return "JUN";
        if (month == 7)
            return "JUL";
        if (month == 8)
            return "AUG";
        if (month == 9)
            return "SEP";
        if (month == 10)
            return "OCT";
        if (month == 11)
            return "NOV";
        if (month == 12)
            return "DEC";

        //default should never happen
        return "JAN";
    }

    public void openDatePicker(View view) {
        datePickerDialog.show();
    }
    public void openDogDatePicker(View view) {
        dogDatePickerDialog.show();
    }

    public void handleAvailability() {
        if (availability != null) {
            switch (availability) {
                case "One day a week":
                    availabilitySpinner.setSelection(0);
                    break;
                case "Two days a week":
                    availabilitySpinner.setSelection(1);
                    break;
                case "Three days a week":
                    availabilitySpinner.setSelection(2);
                    break;
                case "Four days a week":
                    availabilitySpinner.setSelection(3);
                    break;
                case "Five days a week":
                    availabilitySpinner.setSelection(4);
                    break;
                case "Six days a week":
                    availabilitySpinner.setSelection(5);
                    break;
            }
        } else {
            // trigger onDataChange to get userGender

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Tempi").setValue("deleteInAMinute");
            dbRef.child("Tempi").removeValue();
        }
    }

    public void handleDogGender() {
        if (dogGender != null) {
            if (dogGender.equals("male")) {
                if (dogGenderSpinner != null)
                    dogGenderSpinner.setSelection(0);
            } else {
                if (dogGenderSpinner != null)
                    dogGenderSpinner.setSelection(1);
            }
        } else {
            // trigger onDataChange to get userGender

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Tempi").setValue("deleteInAMinute");
            dbRef.child("Tempi").removeValue();
        }
    }

    public void handleUserGender() {
        if (userGender != null) {
            if (userGender.equals("male")) {
                if (userGenderSpinner != null)
                    userGenderSpinner.setSelection(0);
            } else {
                if (userGenderSpinner != null)
                    userGenderSpinner.setSelection(1);
            }
        } else {
            // trigger onDataChange to get userGender

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Tempi").setValue("deleteInAMinute");
            dbRef.child("Tempi").removeValue();
        }
    }


    public void openEditNameDialog(String hint, String strViewToChange) {
        EditNameDialog editNameDialog = new EditNameDialog(hint, strViewToChange);
        editNameDialog.show(getSupportFragmentManager(), null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(CHANGE_PROFILE_CODE == 1){
//            String newProfileImage= data.getStringExtra("result");
//            Uri newProfileImage= (Uri) data.getExtras().get("profilePicture");

//            Uri uri  = Uri.parse(newProfileImage);
//            Picasso.get().load(newProfileImage).into(profileImage);
//            Bundle extras = intent.getExtras();
//            String newProfileImage = extras.getString("NewProfileImage");
//            Uri uri  = Uri.parse(newProfileImage);
//            Picasso.get().load(uri).into(profileImage);
//        }
    }

    public void goToApplyText(String newText, String textViewToApply) {
        switch (textViewToApply) {
            case ("dogs_name"):
                TextView t = findViewById(R.id.dogs_name);
                t.setText(newText);
                addToDogFB("Name", newText);
                break;

//            case ("your_age"):
//                TextView t2 = findViewById(R.id.your_age);
//                t2.setText(newText);
//                addToUserFB("Age", newText);
//                break;

            case ("dogs_age"):
                TextView t3 = findViewById(R.id.dogs_age);
                t3.setText(newText);
                addToDogFB("Age", newText);
                break;

            case ("your_gender"):
                //TextView t4 = findViewById(R.id.your_gender);
                //t4.setText(newText);
                //addToUserFB("Gender", newText);
                break;

            case ("location"): /** TODO: handle with GPS later*/
                TextView t5 = findViewById(R.id.location);
                t5.setText(newText);
                break;
        }
    }

    public void addToUserFB(String attribute, String newText) {
        FirebaseDatabase.getInstance().getReference().child("Users")
                .child(FirebaseAuth.getInstance().getUid().toString())
                .child(attribute).setValue(newText);
    }

    public void addToDogFB(String attribute, String newText) {
        FirebaseDatabase.getInstance().getReference().child("Dogs")
                .child(FirebaseAuth.getInstance().getUid().toString())
                .child(attribute).setValue(newText);
    }

    boolean isFacebookUser() {
        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String choice = adapterView.getItemAtPosition(i).toString();
        switch (adapterView.getId()) {
            case R.id.availability:
                if (availability != null) {
                    availability = choice;
                    addToUserFB("Availability", choice);
                }
                break;
/*            case R.id.dogs_gender:
                if (dogGender != null) {
                    dogGender = choice;
                    addToDogFB("Gender", choice);
                }
                break;*/
            case R.id.your_gender:
                if (userGender != null) {
                    userGender = choice;
                    addToUserFB("Gender", choice);
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // checks whether there is google api service, without it maps won't work.
    @Override
    protected void onPostResume() {
        super.onPostResume();
        int errorCode = GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = GoogleApiAvailability.getInstance()
                    .getErrorDialog(this, errorCode, errorCode, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            Toast.makeText(Profile.this, "no services", Toast.LENGTH_LONG).show();
                        }
                    });
            errorDialog.show();
        }
//        else
//            Toast.makeText(Profile.this, "there's services", Toast.LENGTH_LONG).show();
    }
}