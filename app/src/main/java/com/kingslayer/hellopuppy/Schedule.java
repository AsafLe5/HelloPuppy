package com.kingslayer.hellopuppy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ListenableWorker;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Schedule extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Button chooseShifts;
    private String groupId;
//    private boolean isManager = false;
    private android.os.Handler customHandler;
    int toMins = 1000 * 60;
//    private boolean amIManager = false;
    private String myGroupId;
    private String groupManagerId;
    private String[][] creditsOnDays;
    private int numOfMembers;
    Map<String, Integer> credits = new HashMap<String, Integer>(); // Uid to credits left
    List<String> chosenDays = new ArrayList<>(7); //each cell contain uid of selected user chosen this day.
//    boolean isUpdated = false;
    boolean firstTime = true;
    private List<TextView> namePerRow;
    private Map<String, String> groupNames;
    private TableLayout tableLayout;
    private TextView notReadyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        tableLayout = findViewById(R.id.table_layout);
        notReadyText = findViewById(R.id.not_ready_text);
        getSupportActionBar().setTitle(Constants.SCHEDULE_TITLE);
        namePerRow = new ArrayList<>();
        bottomNavigationView = findViewById(R.id.bottom_navigator);
        bottomNavigationView.setSelectedItemId(R.id.schedule);
        chooseShifts = findViewById(R.id.choose_shifts);
        namePerRow.add(findViewById(R.id.row1_name));
        namePerRow.add(findViewById(R.id.row2_name));
        namePerRow.add(findViewById(R.id.row3_name));
        namePerRow.add(findViewById(R.id.row4_name));
        namePerRow.add(findViewById(R.id.row5_name));
        namePerRow.add(findViewById(R.id.row6_name));
        namePerRow.add(findViewById(R.id.row7_name));
        groupNames = new HashMap<>();
        chooseShifts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupId != null) {
                    Intent intent = new Intent(getApplicationContext(), ChooseShifts.class);
                    intent.putExtra(Constants.GROUP_ID_DB, groupId);
                    startActivity(intent);
                }
            }
        });

        String myId = FirebaseAuth.getInstance().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                assert myId != null;
                if (snapshot.child(Constants.USERS_DB).hasChild(myId) &&
                        snapshot.child(Constants.USERS_DB).child(myId).hasChild(Constants.GROUP_ID_DB)) {
                    groupId = snapshot.child(Constants.USERS_DB).child(myId).child(Constants.GROUP_ID_DB).getValue().toString();
                    if (snapshot.child(Constants.GROUPS_DB).child(groupId).child("groupManagerId").getValue()
                            .toString().equals(myId)) {
                        tableLayout.setVisibility(View.GONE);
                        notReadyText.setVisibility(View.VISIBLE);
                        customHandler = new android.os.Handler();
                        Calendar calNow = Calendar.getInstance();
                        Calendar calNextWed = Calendar.getInstance();
                        calNextWed.set(Calendar.HOUR, 8);
                        calNextWed.set(Calendar.MINUTE, 0);
                        calNextWed.set(Calendar.SECOND, 0);
                        while (calNextWed.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY) {
                            calNextWed.add(Calendar.DATE, 1);
                        }
                        if (calNextWed.get(Calendar.DAY_OF_WEEK) == Calendar.THURSDAY &&
                                (calNextWed.get(Calendar.HOUR) < calNow.get(Calendar.HOUR) ||
                                        (calNextWed.get(Calendar.HOUR) == calNow.get(Calendar.HOUR) &&
                                                calNextWed.get(Calendar.MINUTE) < calNow.get(Calendar.MINUTE)))) {
                            calNextWed.add(Calendar.DATE, 7);
                        }
                        System.out.println(calNextWed.getTimeInMillis() - calNow.getTimeInMillis());
                        long diff = calNextWed.getTimeInMillis() - calNow.getTimeInMillis();
//                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
//                        String newtime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(calNextWed.getTime());
                        /////////////////////customHandler.postDelayed(updateTimerThread, 100000);
                        // Haven't calculated shifts for this week yet
                        if (snapshot.child(Constants.GROUPS_DB).child(groupId)
                                .hasChild("Already calculated this week")) {
                            if (snapshot.child(Constants.GROUPS_DB).child(groupId)
                                    .child("Already calculated this week").getValue().equals("False")) {
                                // manager entered group between Wednesday at 8pm and Sunday
                                if (diff > 1000 * 60 * 60 * 92) {
                                    arrangeShifts();
                                    tableLayout.setVisibility(View.VISIBLE);
                                    notReadyText.setVisibility(View.GONE);
                                    reference.child("Groups").child(groupId).child("Already calculated this week").setValue("True");
                                }
                            }
                            // Already calculated this week
                            else {
                                // manager entered group between Sunday and Wednesday at 8pm
                                if (diff < 1000 * 60 * 60 * 92) {
                                    reference.child("Groups").child(groupId).child("Already calculated this week").setValue("False");
                                }
                                // show already calculated results shifts for this week
                                else {
                                    getShifts(); /// must take from DBBBBB!
                                    tableLayout.setVisibility(View.VISIBLE);
                                    notReadyText.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            reference.child(Constants.GROUPS_DB).child(groupId)
                                    .child("Already calculated this week").setValue("False");
                        }

                        //checkIfAlreadyCalcShiftsThisWeek(groupId);
//                        firstTime = false;
                        //}
                    }
                }
//                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //region $ Navigation View
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.group:
                        startActivity(new Intent(getApplicationContext(), Group.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.schedule:
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
                return false;
            }
        });
        //endregion


    }

    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            //write here whaterver you want to repeat
//            Date currentTime = Calendar.getInstance().getTime();
//            System.out.println("hey");

            arrangeShifts();
            customHandler.postDelayed(this, 2 * toMins);
        }
    };


    private void arrangeShifts() {

        String myId = FirebaseAuth.getInstance().getUid().toString();

        DatabaseReference groupIdRef = FirebaseDatabase.getInstance().getReference();
/*
        // trigger onDataChange
        groupIdRef.child("Tempi").setValue("deleteInAMinute");
        groupIdRef.child("Tempi").removeValue();*/

        groupIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myGroupId = snapshot.child(Constants.USERS_DB).child(myId)
                        .child(Constants.GROUP_ID_DB).getValue().toString();
                List<String> members = (List<String>) snapshot.child(Constants.GROUPS_DB).child(myGroupId)
                        .child("MembersIds").getValue();
                numOfMembers = members.size();

                creditsOnDays = new String[numOfMembers][9];

                groupManagerId = snapshot.child(Constants.GROUPS_DB).child(myGroupId)
                        .child("groupManagerId").getValue().toString();
                if (myId.equals(groupManagerId)) { // I'm the manager

                    int i = 0;

                    if (snapshot.child(Constants.GROUPS_DB).child(myGroupId)
                            .child(Constants.SCHEDULED_CHOICES_DB).child(FirebaseAuth.getInstance().getUid())
                            .getChildrenCount() == 8) {
                        for (DataSnapshot user : snapshot.child(Constants.GROUPS_DB).child(myGroupId)
                                .child(Constants.SCHEDULED_CHOICES_DB).getChildren()) {
                            groupNames.put(user.getKey(), snapshot.child(Constants.USERS_DB)
                                    .child(user.getKey()).child(Constants.USER_NAME_DB).getValue().toString());
                            for (DataSnapshot day : snapshot.child(Constants.GROUPS_DB).child(myGroupId)
                                    .child(Constants.SCHEDULED_CHOICES_DB).child(user.getKey()).getChildren()) {
                                writeInArray(i, user.getKey(), day.getKey(), day.getValue().toString());
                            }
                            i++;
                        }
                        String algorithmData = getString();
                        schedulesAlgorithm(algorithmData);
                        saveInDb();
                        arrangeShiftInTable();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    private String getString() {
        String data = "";
        for (int i = 0; i < numOfMembers; i++) {
            for (int k = 0; k < 9; k++) {
                data = data.concat(creditsOnDays[i][k]);
                data = data.concat(",");
            }
            data = data.concat("\n");
        }
        return data;
    }

    private void saveInDb() {
        // update all credits
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(myGroupId);
        for (int i = 0; i < numOfMembers; i++) {
            String user = (String) credits.keySet().toArray()[i];
            Integer creditsLeft = credits.get(user);
            groupRef.child("ScheduleChoices").child(user).child("Credits").setValue((int) creditsLeft + 5);
        }

        // save schedule for next week
        groupRef.child("NextWeekShifts").setValue(chosenDays);
    }


    public void writeInArray(int row, String user, String day, String choice) {
        creditsOnDays[row][0] = user;
        switch (day) {
            case "Sunday":
                creditsOnDays[row][2] = choice;
                break;

            case "Monday":
                creditsOnDays[row][3] = choice;
                break;

            case "Tuesday":
                creditsOnDays[row][4] = choice;
                break;

            case "Wednesday":
                creditsOnDays[row][5] = choice;
                break;

            case "Thursday":
                creditsOnDays[row][6] = choice;
                break;

            case "Friday":
                creditsOnDays[row][7] = choice;
                break;

            case "Saturday":
                creditsOnDays[row][8] = choice;
                break;

            case "Credits":
                creditsOnDays[row][1] = choice;
                break;
        }
    }


    void schedulesAlgorithm(String choices) {
        Random rand = new Random();
        int[] results = new int[7];
        List<String> userIds = new ArrayList<>();
        String[] userChoices = choices.split("\n?\n|\r");
        int[] userCredits = new int[userChoices.length];
        int[] sundayCredits = new int[userChoices.length];
        int[] mondayCredits = new int[userChoices.length];
        int[] tuesdayCredits = new int[userChoices.length];
        int[] thursdayCredits = new int[userChoices.length];
        int[] wednesdayCredits = new int[userChoices.length];
        int[] fridayCredits = new int[userChoices.length];
        int[] saturdayCredits = new int[userChoices.length];
        for (int i = 0; i < userChoices.length; i++) {
            userIds.add(userChoices[i].split(",")[0]);
            userCredits[i] = Integer.parseInt(userChoices[i].split(",")[1]);
            sundayCredits[i] = Integer.parseInt(userChoices[i].split(",")[2]);
            mondayCredits[i] = Integer.parseInt(userChoices[i].split(",")[3]);
            tuesdayCredits[i] = Integer.parseInt(userChoices[i].split(",")[4]);
            thursdayCredits[i] = Integer.parseInt(userChoices[i].split(",")[5]);
            wednesdayCredits[i] = Integer.parseInt(userChoices[i].split(",")[6]);
            fridayCredits[i] = Integer.parseInt(userChoices[i].split(",")[7]);
            saturdayCredits[i] = Integer.parseInt(userChoices[i].split(",")[8]);
        }
        int[] max = {-3, -3, -3, -3, -3, -3, -3};
        int[] maxUser = {-1, -1, -1, -1, -1, -1, -1};
        for (int i = 0; i < userChoices.length; i++) { // passing through everyone choice for each day
            if (sundayCredits[i] > max[0]) {
                max[0] = sundayCredits[i];
                maxUser[0] = i;
            } else if (sundayCredits[i] == max[0]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[0] = i;
            }
            if (mondayCredits[i] > max[1]) {
                max[1] = mondayCredits[i];
                maxUser[1] = i;
            } else if (sundayCredits[i] == max[1]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[1] = i;
            }
            if (tuesdayCredits[i] > max[2]) {
                max[2] = tuesdayCredits[i];
                maxUser[2] = i;
            } else if (sundayCredits[i] == max[2]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[2] = i;
            }
            if (thursdayCredits[i] > max[3]) {
                max[3] = thursdayCredits[i];
                maxUser[3] = i;
            } else if (sundayCredits[i] == max[3]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[3] = i;
            }
            if (wednesdayCredits[i] > max[4]) {
                max[4] = wednesdayCredits[i];
                maxUser[4] = i;
            } else if (sundayCredits[i] == max[4]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[4] = i;
            }
            if (fridayCredits[i] > max[5]) {
                max[5] = fridayCredits[i];
                maxUser[5] = i;
            } else if (sundayCredits[i] == max[5]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[5] = i;
            }
            if (saturdayCredits[i] > max[6]) {
                max[6] = saturdayCredits[i];
                maxUser[6] = i;
            } else if (sundayCredits[i] == max[6]) {
                int r = rand.nextInt(2);
                if (r == 0)
                    maxUser[6] = i;
            }
        }

        for (int i = 0; i < 7; i++) {
            int userI = maxUser[i];
            userCredits[userI] -= Math.abs(max[i]);
            this.chosenDays.add(userIds.get(maxUser[i]));
            this.credits.put(userIds.get(maxUser[i]), userCredits[userI]);
        }
        int a = 0;
    }

    private void arrangeShiftInTable() {
        for (int i = 0; i < namePerRow.size(); i++) {
            namePerRow.get(i).setText(groupNames.get(chosenDays.get(i)));
        }
    }

    private void getShifts() {
        String myId = FirebaseAuth.getInstance().getUid().toString();
        DatabaseReference groupIdRef = FirebaseDatabase.getInstance().getReference();

        groupIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myGroupId = snapshot.child("Users").child(myId)
                        .child(Constants.GROUP_ID_DB).getValue().toString();
                // get all get group member's names.
                for (DataSnapshot user : snapshot.child("Groups").child(myGroupId)
                        .child("ScheduleChoices").getChildren()) {
                    groupNames.put(user.getKey().toString(), snapshot.child("Users")
                            .child(user.getKey()).child("Full name").getValue().toString());

                    chosenDays = (List<String>) snapshot.child("Groups")
                            .child(groupId).child("NextWeekShifts").getValue();

                    arrangeShiftInTable();

                }
            }


                @Override
                public void onCancelled (@NonNull @NotNull DatabaseError error){

                }
            });

        }

    }