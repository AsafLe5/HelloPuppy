package com.kingslayer.hellopuppy.Models;

public class ModelGroupChat {
    String timestamp, message, sender, type;


    ModelGroupChat(){

    }

    ModelGroupChat(String timestamp, String message, String sender, String type){
        this.message = message;
        this.sender = sender;
        this.timestamp = timestamp;
        this.type = type;
    }

    //region $ getters
    public String getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getType() {
        return type;
    }
    //endregion

    //region $ setters
    public void setMessage(String message) {
        this.message = message;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }
    //endregion
}
