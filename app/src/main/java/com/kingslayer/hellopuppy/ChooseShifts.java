package com.kingslayer.hellopuppy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ChooseShifts extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner sundaySpinner;
    private Spinner mondaySpinner;
    private Spinner tuesdaySpinner;
    private Spinner wednesdaySpinner;
    private Spinner thursdaySpinner;
    private Spinner fridaySpinner;
    private Spinner saturdaySpinner;

    private String sundayChoice;
    private String mondayChoice;
    private String tuesdayChoice;
    private String wednesdayChoice;
    private String thursdayChoice;
    private String fridayChoice;
    private String saturdayChoice;

    private Button submit;
    private String groupId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shifts);

        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }

        sundaySpinner = findViewById(R.id.sunday_spinner);
        ArrayAdapter<CharSequence> sundayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        sundayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sundaySpinner.setAdapter(sundayAdapter);
        sundaySpinner.setOnItemSelectedListener(this);

        mondaySpinner = findViewById(R.id.monday_spinner);
        ArrayAdapter<CharSequence> mondayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        mondayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mondaySpinner.setAdapter(mondayAdapter);
        mondaySpinner.setOnItemSelectedListener(this);

        tuesdaySpinner = findViewById(R.id.tuesday_spinner);
        ArrayAdapter<CharSequence> tuesdayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        tuesdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tuesdaySpinner.setAdapter(tuesdayAdapter);
        tuesdaySpinner.setOnItemSelectedListener(this);

        wednesdaySpinner = findViewById(R.id.wednesday_spinner);
        ArrayAdapter<CharSequence> wednesdayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        wednesdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wednesdaySpinner.setAdapter(wednesdayAdapter);
        wednesdaySpinner.setOnItemSelectedListener(this);

        thursdaySpinner = findViewById(R.id.thursday_spinner);
        ArrayAdapter<CharSequence> thursdayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        thursdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thursdaySpinner.setAdapter(thursdayAdapter);
        thursdaySpinner.setOnItemSelectedListener(this);

        fridaySpinner = findViewById(R.id.friday_spinner);
        ArrayAdapter<CharSequence> fridayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        fridayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fridaySpinner.setAdapter(fridayAdapter);
        fridaySpinner.setOnItemSelectedListener(this);

        saturdaySpinner = findViewById(R.id.saturday_spinner);
        ArrayAdapter<CharSequence> saturdayAdapter = ArrayAdapter.
                createFromResource(this,R.array.preferences, android.R.layout.simple_spinner_item);
        saturdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        saturdaySpinner.setAdapter(saturdayAdapter);
        saturdaySpinner.setOnItemSelectedListener(this);

        submit = findViewById(R.id.submit_shifts);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // groups -> this group ->wanted shifts list
                // -> for every person save each day and what he wants

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Sunday").setValue(sundayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Monday").setValue(mondayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Tuesday").setValue(tuesdayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Wednesday").setValue(wednesdayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Thursday").setValue(thursdayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Friday").setValue(fridayChoice);

                FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString()).child("Saturday").setValue(saturdayChoice);


                Toast.makeText(ChooseShifts.this, "Your preferences submitted successfully!"
                        , Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), Schedule.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String choice = parent.getItemAtPosition(position).toString();
        switch (parent.getId()){
            case R.id.sunday_spinner:
                sundayChoice = choice;
                break;
            case R.id.monday_spinner:
                mondayChoice = choice;
                break;
            case R.id.tuesday_spinner:
                tuesdayChoice = choice;
                break;
            case R.id.wednesday_spinner:
                wednesdayChoice = choice;
                break;
            case R.id.thursday_spinner:
                thursdayChoice = choice;
                break;
            case R.id.friday_spinner:
                fridayChoice = choice;
                break;
            case R.id.saturday_spinner:
                saturdayChoice = choice;
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}