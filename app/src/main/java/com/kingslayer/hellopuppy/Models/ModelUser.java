package com.kingslayer.hellopuppy.Models;

import android.net.Uri;

public class ModelUser {
    private String userName, dogsName, availability, userId;
    private String userProfile;


    public ModelUser(String userId){
        this.userId = userId;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName(){
        return userName;
    }

    public String getDogsName() {
        return dogsName;
    }

    public String getAvailability() {
        return availability;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDogsName(String dogsName) {
        this.dogsName = dogsName;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
