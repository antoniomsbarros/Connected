package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.time.LocalDateTime;
import java.util.Date;

public class Message {
    private String receiverId;
    private String senderId;
    private String message;
    private String date;

    public Message(String receiverId, String senderId, String message, LocalDateTime date) {
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.message = message;
        this.date = date.toString();
    }

    public Message(){}

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
