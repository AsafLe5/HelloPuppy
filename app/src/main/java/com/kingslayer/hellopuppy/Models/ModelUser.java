package com.kingslayer.hellopuppy.Models;

public class ModelUser {
    String userName, dogsName, availability, userId;


    public ModelUser(String userId){
        this.userId = userId;
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
