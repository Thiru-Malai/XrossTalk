package com.example.chatapp.Model;

import static java.text.DateFormat.getDateTimeInstance;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public class Chats {

    String sender, reciever, message, mood;
    boolean isseen;
    String time;
    String msgTime;

    public Chats(String sender, String reciever, String message, boolean isseen, String mood, String msgTime, String time) {
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.isseen = isseen;
        this.msgTime = msgTime;
        this.mood = mood;
        this.time = time;

    }


    public Chats() {
    }

    public static String getTime(long timestamp) {
        try {
            DateFormat dateFormat = getDateTimeInstance();
            Date netDate = (new Date(timestamp));
            return dateFormat.format(netDate);
        } catch (Exception e) {
            return "date";
        }
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
