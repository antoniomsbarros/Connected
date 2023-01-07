package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.Date;

public class Event {
    private String name;
    private String description;
    private Date date;
    private String image;
    private String latitude;
    private String longitude;
    private final String uuidUser;
    private final Group attendingMembers;

    public Event(String name, String description, Date date, String image, String latitude, String longitude, String uuidUser) {
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

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
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

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void addUser(String uuidUser) {
        this.attendingMembers.addUser(uuidUser);
    }
}
