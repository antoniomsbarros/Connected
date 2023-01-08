package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.Date;

public class Event {
    private static final String NO_USER = "N/A";

    private String name;
    private String description;
    private Date date;
    private String image;
    private double latitude;
    private double longitude;
    private final String uuidUser;
    private final Group attendingMembers;

    public Event() {
        this.uuidUser = NO_USER;
        this.attendingMembers = new Group();
    }

    public Event(String name, String description, Date date, String image, double latitude, double longitude, String uuidUser) {
        this.name = name;
        this.description = description;
        this.date = date;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.uuidUser = uuidUser;
        this.attendingMembers = new Group(uuidUser);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public String getImage() {
        return image;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUuidUser() {
        return uuidUser;
    }

    public Group getAttendingMembers() {
        return attendingMembers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void addUser(String uuidUser) {
        this.attendingMembers.addUser(uuidUser);
    }
}
