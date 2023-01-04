package com.example.simov_project_1180874_1191455__1200606.Entity;

public class Post {
    private String ImageURL;
    private String UuidUser;
    private String latitude;
    private String longitude;

    public Post(String imageURL, String uuidUser, String latitude, String longitude) {
        ImageURL = imageURL;
        UuidUser = uuidUser;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Post() {
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String imageURL) {
        ImageURL = imageURL;
    }

    public String getUuidUser() {
        return UuidUser;
    }

    public void setUuidUser(String uuidUser) {
        UuidUser = uuidUser;
    }


}
