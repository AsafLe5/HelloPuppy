package com.kingslayer.hellopuppy;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MakeShifts extends Worker {

    private boolean amIManager = false;
    private String myGroupId;
    private String groupManagerId;
    private String[][] creditsOnDays;
    private int numOfMembers;
    Map<String, Integer> credits = new HashMap<String, Integer>(); // Uid to credits left
    List<String> chosenDays = new ArrayList<>(7); //each cell contain uid of selected user chosen this day.
    boolean isUpdated = false;


    public MakeShifts(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams){
        super(context, workerParams);
    }


    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        String myId = FirebaseAuth.getInstance().getUid().toString();

        DatabaseReference groupIdRef = FirebaseDatabase.getInstance().getReference();

        // trigger onDataChange
        groupIdRef.child("Tempi").setValue("deleteInAMinute");
        groupIdRef.child("Tempi").removeValue();

        groupIdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                myGroupId = snapshot.child("Users").child(myId)
                        .child(Constants.GROUP_ID_DB).getValue().toString();
                List<String> members = (List<String>) snapshot.child("Groups").child(myGroupId)
                        .child("MembersIds").getValue();
                numOfMembers = members.size();

                creditsOnDays = new String[numOfMembers][9];

                groupManagerId = snapshot.child("Groups").child(myGroupId)
                        .child("groupManagerId").getValue().toString();
                if (myId.equals(groupManagerId)) { // I'm the manager

                    int i = 0;

                    if (snapshot.child("Groups").child(myGroupId)
                            .child("ScheduleChoices").child(FirebaseAuth.getInstance().getUid().toString())
                            .getChildrenCount() == 8) {
                        for (DataSnapshot user : snapshot.child("Groups").child(myGroupId)
                                .child("ScheduleChoices").getChildren()) {

                                for (DataSnapshot day : snapshot.child("Groups").child(myGroupId)
                                        .child("ScheduleChoices").child(user.getKey().toString()).getChildren()) {
                                    writeInArray(i, user.getKey().toString(), day.getKey().toString(), day.getValue().toString());
                                }
                                i++;
                        }
                        String algorithmData = getString();
                        schedulesAlgorithm(algorithmData);
                        saveInDb();
                    }
                }
            }


            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

//        if (credits.size() > 0 && chosenDays.size()>0) {
//            saveInDb();
////            isUpdated = true;
//        }
//        else {
//
//        }
        return Result.success();
    }

    private String getString() {
        String data = "";
        for(int i = 0; i< numOfMembers; i++){
            for (int k = 0; k< 9; k++){
                data = data.concat(creditsOnDays[i][k]);
                data = data.concat(",");
            }
            data= data.concat("\n");
        }
        return data;
    }

    private void saveInDb(){
        // update all credits
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(myGroupId);
        for(int i=0; i< numOfMembers; i++){
            String user = (String) credits.keySet().toArray()[i];
            Integer creditsLeft = credits.get(user);
            groupRef.child("ScheduleChoices").child(user).child("Credits").setValue(creditsLeft + 5);
        }

        // save schedule for next week
        groupRef.child("NextWeekShifts").setValue(chosenDays);
    }


    public void writeInArray(int row, String user, String day, String choice){
        creditsOnDays[row][0] = user;
        switch (day){
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
        int[] max = {-3,-3,-3,-3,-3,-3,-3};
        int[] maxUser = {-1,-1,-1,-1,-1,-1,-1};
        for (int i = 0; i < userChoices.length; i++){ // passing through everyone choice for each day
            if (sundayCredits[i]>max[0]){
                max[0] = sundayCredits[i];
                maxUser[0] = i;
            }
            else if (sundayCredits[i]==max[0]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[0] = i;
            }
            if (mondayCredits[i]>max[1]) {
                max[1] = mondayCredits[i];
                maxUser[1] = i;
            }
            else if (sundayCredits[i]==max[1]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[1] = i;
            }
            if (tuesdayCredits[i]>max[2]) {
                max[2] = tuesdayCredits[i];
                maxUser[2] = i;
            }
            else if (sundayCredits[i]==max[2]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[2] = i;
            }
            if (thursdayCredits[i]>max[3]) {
                max[3] = thursdayCredits[i];
                maxUser[3] = i;
            }
            else if (sundayCredits[i]==max[3]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[3] = i;
            }
            if (wednesdayCredits[i]>max[4]) {
                max[4] = wednesdayCredits[i];
                maxUser[4] = i;
            }
            else if (sundayCredits[i]==max[4]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[4] = i;
            }
            if (fridayCredits[i]>max[5]) {
                max[5] = fridayCredits[i];
                maxUser[5] = i;
            }
            else if (sundayCredits[i]==max[5]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[5] = i;
            }
            if (saturdayCredits[i]>max[6]) {
                max[6] = saturdayCredits[i];
                maxUser[6] = i;
            }
            else if (sundayCredits[i]==max[6]){
                int r = rand.nextInt(2);
                if (r==0)
                    maxUser[6] = i;
            }
        }

        for (int i = 0; i < 7; i++){
            int userI = maxUser[i];
            userCredits[userI] -= Math.abs(max[i]);
            this.chosenDays.add(userIds.get(maxUser[i]));
            this.credits.put(userIds.get(maxUser[i]),userCredits[userI]);
        }
        int a= 0;
    }
}