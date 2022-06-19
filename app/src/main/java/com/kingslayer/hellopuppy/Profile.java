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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
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
   //private Button buttonEditLocation;
    private Button buttonEditAvailability;
    private String profileImageStr;
    private Uri profileImageUri;
    private ImageView profileImage;
    private TextView dogsName;
    FloatingActionButton addProfileImage;
    //    private Intent intent;
    private Spinner userGenderSpinner;
    private Spinner isVaccinatedSpinner;
    private Spinner isCastratedSpinner;
    private Spinner availabilitySpinner;
    private Spinner locationSpinner;
    private Spinner dogGenderSpinner;
//    private TextView usersAge;
    private TextView dogsAge;
    BottomNavigationView bottomNavigationView;
    View _rootView;
    private String userGender;
    private String isVaccinated;
    private String isCastrated;
    private String availability;
    private String myLocation;
    private String dogGender;
    private DatePickerDialog datePickerDialog;
    private DatePickerDialog dogDatePickerDialog;
    private Button dateButton;
    private Button dogDateButton;
    private boolean allFieldsGotFilled = false;
    private boolean hasGroup = false;

    private Boolean isDogPic = false;

    private Uri imageUri;
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    private ImageView dogImage;


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
        //dogsAge = findViewById(R.id.dogs_age);

        initUserGenderSpinner();
        initDogGenderSpinner();
        initIsVaccinatedSpinner();
        initIsCastratedSpinner();



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


                if (dog.hasChild("Name")) {
                    dogsName.setText(dog.child("Name").getValue().toString());
                    numOfFilledFields++;
                }

//                if (dog.hasChild("Age")) {
//                    dogsAge.setText(dog.child("Age").getValue().toString());
//                    numOfFilledFields++;
//
//                }

                if (user.hasChild("Gender")) {
                    userGender = user.child("Gender").getValue().toString();
                    handleUserGender();
                    numOfFilledFields++;
                } /*else {
                    userGenderSpinner.setSelection(0);
                    addToUserFB("Gender", "male");
                }*/

                if (user.hasChild("Is vaccinated")) {
                    isVaccinated = user.child("Is vaccinated").getValue().toString();
                    handleIsVaccinated();
                    numOfFilledFields++;
                }

                if (user.hasChild("Is castrated")) {
                    isCastrated = user.child("Is castrated").getValue().toString();
                    handleIsCastrated();
                    numOfFilledFields++;
                }

                if (user.hasChild("Availability")) {
                    availability = user.child("Availability").getValue().toString();
                    handleAvailability();
                    numOfFilledFields++;
                } else {
                    availabilitySpinner.setSelection(0);
                    addToUserFB("Availability", "One day a week");
                }

                if (user.hasChild("Location")) {
                    myLocation = user.child("Location").getValue().toString();
                    handleLocation();
                    numOfFilledFields++;
                } else {
                    locationSpinner.setSelection(0);
                    addToUserFB("Location", "Haven't choose yet");
                }

                if (dog.hasChild("Gender")) {
                    dogGender = dog.child("Gender").getValue().toString();
                    handleDogGender();
                    numOfFilledFields++;
                }/* else {
                    dogGenderSpinner.setSelection(0);
                    addToDogFB("Gender", "male");
                }*/
                if (numOfFilledFields >= 9){
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

        handleUserGender();
        handleIsVaccinated();
        handleIsCastrated();
        availabilitySpinner = findViewById(R.id.availability);
        ArrayAdapter<CharSequence> availabilityAdapter = ArrayAdapter.
                createFromResource(this, R.array.availabilities, android.R.layout.simple_spinner_item);
        availabilityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        availabilitySpinner.setAdapter(availabilityAdapter);
        availabilitySpinner.setOnItemSelectedListener(this);
        handleAvailability();

        locationSpinner = findViewById(R.id.location_spinner);
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.
                createFromResource(this, R.array.cities, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(this);
        handleLocation();


        buttonEditDogsBreed = findViewById(R.id.buttonEditBreed);
        buttonEditDogsBreed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditNameDialog("Enter dog's breed",
                        "dogs_breed");
            }
        });
    }

    private void initDogGenderSpinner() {
        dogGenderSpinner = (Spinner) findViewById(R.id.dog_gender_spinner);
        ArrayAdapter<String> dogGenderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        dogGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dogGenderAdapter.add("Male");
        dogGenderAdapter.add("Female");
        dogGenderAdapter.add("Choose dog's gender"); //This is the text that will be displayed as hint.

        dogGenderSpinner.setAdapter(dogGenderAdapter);
        dogGenderSpinner.setSelection(dogGenderAdapter.getCount()); //set the hint the default selection so it appears on launch.
        dogGenderSpinner.setOnItemSelectedListener(this);
    }

    private void initUserGenderSpinner() {
        userGenderSpinner = (Spinner) findViewById(R.id.user_gender_spinner);
        ArrayAdapter<String> userGenderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        userGenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userGenderAdapter.add("Male");
        userGenderAdapter.add("Female");
        userGenderAdapter.add("Choose your gender"); //This is the text that will be displayed as hint.

        userGenderSpinner.setAdapter(userGenderAdapter);
        userGenderSpinner.setSelection(userGenderAdapter.getCount()); //set the hint the default selection so it appears on launch.
        userGenderSpinner.setOnItemSelectedListener(this);
    }

    private void initIsVaccinatedSpinner() {
        isVaccinatedSpinner = (Spinner) findViewById(R.id.is_vaccinated_spinner);
        ArrayAdapter<String> isVaccinatedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        isVaccinatedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isVaccinatedAdapter.add("Yes");
        isVaccinatedAdapter.add("No");
        isVaccinatedAdapter.add("Yes or no"); //This is the text that will be displayed as hint.

        isVaccinatedSpinner.setAdapter(isVaccinatedAdapter);
        isVaccinatedSpinner.setSelection(isVaccinatedAdapter.getCount()); //set the hint the default selection so it appears on launch.
        isVaccinatedSpinner.setOnItemSelectedListener(this);
    }

    private void initIsCastratedSpinner() {
        isCastratedSpinner = (Spinner) findViewById(R.id.is_castrated_spinner);
        ArrayAdapter<String> isCastratedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        isCastratedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        isCastratedAdapter.add("Yes");
        isCastratedAdapter.add("No");
        isCastratedAdapter.add("Yes or no"); //This is the text that will be displayed as hint.

        isCastratedSpinner.setAdapter(isCastratedAdapter);
        isCastratedSpinner.setSelection(isCastratedAdapter.getCount()); //set the hint the default selection so it appears on launch.
        isCastratedSpinner.setOnItemSelectedListener(this);
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.log_out:
                if(isFacebookUser()){
                    LoginManager.getInstance().logOut();
                }
                else{
                    Login.getmGoogleSignInClient().signOut();
                }
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
        }
        return true;
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

    public void handleLocation() {
        if (myLocation != null) {
            int i = 0;
            switch (myLocation) {
                case "Ẕefat":
                    i++;
                case "Tiberias":
                    i++;
                case "Tel aviv–Yafo":
                    i++;
                case "LeẔiyyon":
                    i++;
                case "Reẖovot":
                    i++;
                case "Ramla":
                    i++;
                case "Ramat Gan":
                    i++;
                case "Shemona":
                    i++;
                case "Netanya":
                    i++;
                case "Nazareth":
                    i++;
                case "Nahariyya":
                    i++;
                case "Meron":
                    i++;
                case "Lod":
                    i++;
                case "Karmiel":
                    i++;
                case "Jerusalem":
                    i++;
                case "Holon":
                    i++;
                case "Herzliyya":
                    i++;
                case "Haifa":
                    i++;
                case "Hadera":
                    i++;
                case "Givatayim":
                    i++;
                case "Elat":
                    i++;
                case "Dor":
                    i++;
                case "Dimona":
                    i++;
                case "Caesarea":
                    i++;
                case "Beersheba":
                    i++;
                case "Ashqelon":
                    i++;
                case "Ashdod":
                    i++;
                case "Arad":
                    i++;
                case "Akko":
                    i++;
                case "Afula":
                    i++;

            }
            locationSpinner.setSelection(i-1);
        } else {
            // trigger onDataChange to get userGender

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Tempi").setValue("deleteInAMinute");
            dbRef.child("Tempi").removeValue();
        }
    }

    public void handleDogGender() {
        if (dogGender != null) {
            if (dogGender.equals("Male")) {
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
            if (userGender.equals("Male")) {
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

    public void handleIsVaccinated() {
        if (isVaccinated != null) {
            if (isVaccinated.equals("Yes")) {
                if (isVaccinatedSpinner != null)
                    isVaccinatedSpinner.setSelection(0);
            } else {
                if (isVaccinatedSpinner != null)
                    isVaccinatedSpinner.setSelection(1);
            }
        } else {
            // trigger onDataChange to get isVaccinated

            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
            dbRef.child("Tempi").setValue("deleteInAMinute");
            dbRef.child("Tempi").removeValue();
        }
    }

    public void handleIsCastrated() {
        if (isCastrated != null) {
            if (isCastrated.equals("Yes")) {
                if (isCastratedSpinner != null)
                    isCastratedSpinner.setSelection(0);
            } else {
                if (isCastratedSpinner != null)
                    isCastratedSpinner.setSelection(1);
            }
        } else {
            // trigger onDataChange to get isCastrated

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

//            case ("dogs_age"):
//                TextView t3 = findViewById(R.id.dogs_age);
//                t3.setText(newText);
//                addToDogFB("Age", newText);
//                break;



//            case ("location"): /** TODO: handle with GPS later*/
//                TextView t5 = findViewById(R.id.location);
//                t5.setText(newText);
//                break;
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
            case R.id.location_spinner:
                if (myLocation != null) {
                    myLocation = choice;
                    addToUserFB("Location", choice);
                }
                break;
            case R.id.dog_gender_spinner:
                if (dogGender != null) {
                    dogGender = choice;
                    addToDogFB("Gender", choice);
                }
                break;

            case R.id.user_gender_spinner:
                if (userGender != null) {
                    userGender = choice;
                    addToUserFB("Gender", choice);
                    break;
                }
            case R.id.is_vaccinated_spinner:
                if (isVaccinated != null) {
                    isVaccinated = choice;
                    addToDogFB("Is vaccinated", choice);
                }
            case R.id.is_castrated_spinner:
                if (isCastrated != null) {
                    isCastrated = choice;
                    addToDogFB("Is castrated", choice);
                }
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