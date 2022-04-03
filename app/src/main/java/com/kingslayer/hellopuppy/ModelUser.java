package com.kingslayer.hellopuppy;

public class ModelUser {
    String userName, dogsName;

    public ModelUser(){
    }

    public ModelUser(String userName, String dogsName){
        this.userName = userName;
        this.dogsName = dogsName;
    }


    public String getUserName(){
        return userName;
    }

    public String getDogsName() {
        return dogsName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDogsName(String dogsName) {
        this.dogsName = dogsName;
    }
}
