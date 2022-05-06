package com.kingslayer.hellopuppy.Models;

import android.widget.ImageView;

import java.net.URL;

public class ModelGroup {
    private String walksPerWeek;
    private String numOfMembers;
    private String explanation;
    private String groupName;
    private String groupId;
//    private URL imageUrl;

    public ModelGroup(){
    }

    public ModelGroup(String groupName, String numOfMembers, String walksPerWeek,
                      String explanation, URL imageUrl, String groupId){
        this.groupName = groupName;
        this.explanation = explanation;
        this.numOfMembers = numOfMembers;
        this.walksPerWeek = walksPerWeek;
//        this.imageUrl = imageUrl;
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
    //endregion
}
