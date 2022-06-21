package com.kingslayer.hellopuppy.Models;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.kingslayer.hellopuppy.R;

import java.net.URL;


public class ModelGroup extends AppCompatActivity {
    private String walksPerWeek;
    private String numOfMembers;
    private String explanation;
    private String groupName;
    private String groupId;
    private Button requestJoinBtn;
    private String groupProfile;

//    private URL imageUrl;

    public ModelGroup()  {
    }

    public ModelGroup(String groupName, String numOfMembers, String walksPerWeek,
                      URL imageUrl, String groupId, String groupProfile){
        this.groupName = groupName;
        this.explanation = explanation;
        this.numOfMembers = numOfMembers;
        this.walksPerWeek = walksPerWeek;
//        this.imageUrl = imageUrl;
        this.groupId = groupId;
        this.groupProfile = groupProfile;
        //this.requestJoinBtn = findViewById(R.id.RequestJoin);

//        requestJoinBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Toast.makeText(Group., "clicked", Toast.LENGTH_SHORT).show();
//                String a = groupId;
//            }
//    });
   }

    public ModelGroup(String groupName, String numOfMembers, String walksPerWeek,
                       URL imageUrl, String groupId){
        this.groupName = groupName;
        this.explanation = explanation;
        this.numOfMembers = numOfMembers;
        this.walksPerWeek = walksPerWeek;
        this.groupId = groupId;
    }

    //region $ getters
    public String getNumOfMembers() {
        return numOfMembers;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getWalksPerWeek() {
        return walksPerWeek;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupProfile() {
        return groupProfile;
    }

    public Button getRequestJoinBtn() {
        return requestJoinBtn;
    }

    //    public URL getImageUrl() {
//        return imageUrl;
//    }
    //endregion

    //region $ setters
    public void setNumOfMembers(String numOfMembers) {
        this.numOfMembers = numOfMembers;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

//    public void setImageUrl(URL imageUrl) {
//        this.imageUrl = imageUrl;
//    }

    public void setWalksPerWeek(String walksPerWeek) {
        this.walksPerWeek = walksPerWeek;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setGroupProfile(String groupProfile) {
        this.groupProfile = groupProfile;
    }
    //endregion
}
