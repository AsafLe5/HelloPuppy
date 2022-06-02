package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, String> choices;

    private int creditsLeft;
    private int progr = 0;
    private ProgressBar progress_bar;
    private int percent;
    private Button submit;
    private String groupId;
    private Map<String, List<String>> daysInventory;
    private Map<String, ArrayAdapter> daysAdapter;

    private Map<String, Spinner> daysSpinner;
    private List<String> sundayInventory = new ArrayList<String>();
    private List<String> mondayInventory = new ArrayList<String>();
    private List<String> tuesdayInventory = new ArrayList<String>();
    private List<String> wednesdayInventory = new ArrayList<String>();
    private List<String> thursdayInventory = new ArrayList<String>();
    private List<String> fridayInventory = new ArrayList<String>();
    private List<String> saturdayInventory = new ArrayList<String>();
    private ArrayAdapter sundayAdapter;
    private ArrayAdapter mondayAdapter;
    private ArrayAdapter tuesdayAdapter;
    private ArrayAdapter wednesdayAdapter;
    private ArrayAdapter thursdayAdapter;
    private ArrayAdapter fridayAdapter;
    private ArrayAdapter saturdayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_shifts);
        daysInventory = new HashMap<>(7);
        daysAdapter = new HashMap<>(7);
        choices = new HashMap<>(7);

        daysSpinner = new HashMap<>(7);
        daysInventory.put("sunday", sundayInventory);
        daysInventory.put("monday", mondayInventory);
        daysInventory.put("tuesday", tuesdayInventory);
        daysInventory.put("wednesday", wednesdayInventory);
        daysInventory.put("thursday", thursdayInventory);
        daysInventory.put("friday", fridayInventory);
        daysInventory.put("saturday", saturdayInventory);

        if (getIntent().hasExtra("GroupId")) {
            Bundle B = getIntent().getExtras();
            groupId = B.getString("GroupId");
        }

        progress_bar = findViewById(R.id.progress_bar);


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupId).child("ScheduleChoices");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()){
                    if(ds.getKey().equals(FirebaseAuth.getInstance().getUid().toString())){
                        creditsLeft = Integer.parseInt(ds.child("Credits").getValue().toString());
                        creditsLeft = 5;
                        percent = 100 / creditsLeft;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        // trigger onDataChange
        reference.child("Tempi").setValue("deleteInAMinute");
        reference.child("Tempi").removeValue();



//        sundayInventory.add("really bad");
//        sundayInventory.add("bad");
//        sundayInventory.add("fine");
//        sundayInventory.add("good");
//        sundayInventory.add("really good");

        for (List<String> day: daysInventory.values()){
            day.add("really bad");
            day.add("bad");
            day.add("fine");
            day.add("good");
            day.add("really good");
        }

        sundaySpinner = findViewById(R.id.sunday_spinner);
        sundayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, sundayInventory);
        sundayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sundaySpinner.setAdapter(sundayAdapter);
        sundaySpinner.setOnItemSelectedListener(this);
        sundaySpinner.setSelection(2);


        mondaySpinner = findViewById(R.id.monday_spinner);
        mondayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, mondayInventory);
        mondayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mondaySpinner.setAdapter(mondayAdapter);
        mondaySpinner.setOnItemSelectedListener(this);
        mondaySpinner.setSelection(2);

        tuesdaySpinner = findViewById(R.id.tuesday_spinner);
        tuesdayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, tuesdayInventory);
        tuesdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tuesdaySpinner.setAdapter(tuesdayAdapter);
        tuesdaySpinner.setOnItemSelectedListener(this);
        tuesdaySpinner.setSelection(2);


        wednesdaySpinner = findViewById(R.id.wednesday_spinner);
        wednesdayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, wednesdayInventory);
        wednesdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wednesdaySpinner.setAdapter(wednesdayAdapter);
        wednesdaySpinner.setOnItemSelectedListener(this);
        wednesdaySpinner.setSelection(2);


        thursdaySpinner = findViewById(R.id.thursday_spinner);
        thursdayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, thursdayInventory);
        thursdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        thursdaySpinner.setAdapter(thursdayAdapter);
        thursdaySpinner.setOnItemSelectedListener(this);
        thursdaySpinner.setSelection(2);


        fridaySpinner = findViewById(R.id.friday_spinner);
        fridayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, fridayInventory);
        fridayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fridaySpinner.setAdapter(fridayAdapter);
        fridaySpinner.setOnItemSelectedListener(this);
        fridaySpinner.setSelection(2);


        saturdaySpinner = findViewById(R.id.saturday_spinner);
        saturdayAdapter = new ArrayAdapter(this,R.layout.support_simple_spinner_dropdown_item, saturdayInventory);
        saturdayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        saturdaySpinner.setAdapter(saturdayAdapter);
        saturdaySpinner.setOnItemSelectedListener(this);
        saturdaySpinner.setSelection(2);
        daysAdapter.put("sunday", sundayAdapter);
        daysAdapter.put("monday", mondayAdapter);
        daysAdapter.put("tuesday", tuesdayAdapter);
        daysAdapter.put("wednesday", wednesdayAdapter);
        daysAdapter.put("thursday", thursdayAdapter);
        daysAdapter.put("friday", fridayAdapter);
        daysAdapter.put("saturday", saturdayAdapter);

        choices.put("sunday", "");
        choices.put("monday", "");
        choices.put("tuesday", "");
        choices.put("wednesday", "");
        choices.put("thursday", "");
        choices.put("friday", "");
        choices.put("saturday", "");


        daysSpinner.put("sunday", sundaySpinner);
        daysSpinner.put("monday", mondaySpinner);
        daysSpinner.put("tuesday", tuesdaySpinner);
        daysSpinner.put("wednesday", wednesdaySpinner);
        daysSpinner.put("thursday", thursdaySpinner);
        daysSpinner.put("friday", fridaySpinner);
        daysSpinner.put("saturday", saturdaySpinner);
//        saturdaySpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });




        submit = findViewById(R.id.submit_shifts);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // groups -> this group ->wanted shifts list
                // -> for every person save each day and what he wants
                DatabaseReference dref = FirebaseDatabase.getInstance().getReference("Groups").child(groupId)
                        .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid()
                        .toString());

                dref.child("Sunday").setValue(sundayChoice);
                dref.child("Monday").setValue(mondayChoice);
                dref.child("Tuesday").setValue(tuesdayChoice);
                dref.child("Wednesday").setValue(wednesdayChoice);
                dref.child("Thursday").setValue(thursdayChoice);
                dref.child("Friday").setValue(fridayChoice);
                dref.child("Saturday").setValue(saturdayChoice);

                dref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.getChildrenCount() == 8) {
                            Toast.makeText(ChooseShifts.this, "Your preferences submitted successfully!"
                                    , Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), Schedule.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

            }
        });
    }

    public String getNumOfCredits(String choice){
        switch (choice){
            case "really bad":
                return "2";

            case "bad":
                return "1";

            case "fine":
                return "0";

            case "good":
                return "-1";

            case "really good":
                return "-2";
            default:
                return "";
        }
    }


    private void updateProgressBar(String times, String before, String day) {
        int prev = Math.abs(Integer.parseInt(before));
        int curr = Math.abs(Integer.parseInt(times));
        progr+=percent*(curr - prev);
        progress_bar.setProgress(progr);

        int minSize = 5;
        for (List<String> tempDay : daysInventory.values()){
            if (minSize> tempDay.size())
                minSize = tempDay.size();
        }

        if (progr <= 100-200/creditsLeft && minSize < 4){
            IncToFiveOpt(curr);
        }

        if (progr <= 100-100/creditsLeft && minSize < 2){
            IncToThreeOpt(curr);
        }

        if (progr > 100-200/creditsLeft && minSize > 4){
            DecToThreeOpt(curr, day);
        }

        if (progr > 100-100/creditsLeft && minSize >2){
            DecToOneOpt(curr, day);
        }
    }

    private void DecToThreeOpt(int choice, String currDay) {

        for (String day : daysInventory.keySet()){
            if(Math.abs(choice) == 2 && !choices.get(day).equals("2") && !choices.get(day).equals("-2") ) {
                daysInventory.get(day).remove(0);
                daysInventory.get(day).remove(3);
            }
        }
        for (String day : daysAdapter.keySet()){
            if(Math.abs(choice) == 2 && !choices.get(day).equals("2") && !choices.get(day).equals("-2")) {
                daysSpinner.get(day).setSelection(1);

            }
        }
        for (ArrayAdapter day: daysAdapter.values()){
           // if(Math.abs(choice) == 2 && !choices.get(day).equals("2") && !choices.get(day).equals("-2"))
                day.notifyDataSetChanged();
        }
    }

    private void DecToOneOpt(int choice, String currDay) {

        for (String day : daysInventory.keySet()){
            if(Math.abs(choice) == 1 && !choices.get(day).equals("1")&& !choices.get(day).equals("-1")
                    && !choices.get(day).equals("-2") && !choices.get(day).equals("2")){
                daysInventory.get(day).remove(0);
                daysInventory.get(day).remove(1);
            }
        }

        for (String day : daysAdapter.keySet()){
            if(Math.abs(choice) == 1 && !choices.get(day).equals("1")&& !choices.get(day).equals("-1")
                && !choices.get(day).equals("-2") && !choices.get(day).equals("2")) {
                daysSpinner.get(day).setSelection(0);
            }
        }

        for (ArrayAdapter day: daysAdapter.values())
            day.notifyDataSetChanged();

    }

    private void IncToThreeOpt(int choice) {


        for (List<String> day : daysInventory.values()){
            if (day.size() == 1){
                day.add(0, "bad");
                day.add("good");
            }
        }

//        for (String day : daysAdapter.keySet()){
//            if(daysInventory.get(day).size() == 3) {
//                daysSpinner.get(day).setSelection(1);
//            }
//        }

        for (ArrayAdapter day: daysAdapter.values())
            day.notifyDataSetChanged();

//        sundayInventory.add(0, "bad");
//        sundayInventory.add("good");
//
//        sundayAdapter.notifyDataSetChanged();
    }

    private void IncToFiveOpt(int choice) {

        for (List<String> day : daysInventory.values()){
            if(day.size() == 3) {
                day.add(0, "really bad");
                day.add("really good");
            }
        }
        for (ArrayAdapter day: daysAdapter.values())
            day.notifyDataSetChanged();

//        sundayInventory.add(0, "really bad");
//        sundayInventory.add( "really good");
//        sundayAdapter.notifyDataSetChanged();

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        boolean nothing = false;
        String temp="0";
        int day;
        String choice = parent.getItemAtPosition(position).toString();
        switch (parent.getId()) {
            case R.id.sunday_spinner:
                if(sundayChoice != null){
                    temp = sundayChoice;
                }
                sundayChoice = getNumOfCredits(choice);
                choices.put("sunday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "sunday");
                break;
            case R.id.monday_spinner:
                if(mondayChoice != null){
                    temp = mondayChoice;
                }
                choices.put("monday" ,getNumOfCredits(choice));
                mondayChoice = getNumOfCredits(choice);
                updateProgressBar(getNumOfCredits(choice), temp, "monday");
                break;
            case R.id.tuesday_spinner:
                if(tuesdayChoice != null){
                    temp = tuesdayChoice;
                }
                tuesdayChoice = getNumOfCredits(choice);
                choices.put("tuesday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "tuesday");
                break;
            case R.id.wednesday_spinner:
                if(wednesdayChoice != null){
                    temp = wednesdayChoice;
                }
                wednesdayChoice = getNumOfCredits(choice);
                choices.put("wednesday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "wednesday");
                break;
            case R.id.thursday_spinner:
                if(thursdayChoice != null){
                    temp = thursdayChoice;
                }
                thursdayChoice = getNumOfCredits(choice);
                choices.put("thursday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "thursday");
                break;
            case R.id.friday_spinner:
                if(fridayChoice != null){
                    temp = fridayChoice;
                }
                fridayChoice = getNumOfCredits(choice);
                choices.put("friday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "friday");
                break;
            case R.id.saturday_spinner:
                if(saturdayChoice != null){
                    temp = saturdayChoice;
                }
                saturdayChoice = getNumOfCredits(choice);
                choices.put("saturday" ,getNumOfCredits(choice));
                updateProgressBar(getNumOfCredits(choice), temp, "saturday");
                break;
            default:
                nothing =true;
                break;
        }
//        if (!nothing)
//            updateProgressBar(getNumOfCredits(choice), temp);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}