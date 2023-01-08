package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Post {

    private String ImageURL;
    private String UuidUser;
    private String email;
    private String fullname;
    private String latitude;
    private String longitude;
    //Arrraylist it the uuid of user that like that post
    public ArrayList<String> likes;
    private final List<Comment> comments;

    public Post(String imageURL, String uuidUser, String email, String fullname, String latitude, String longitude, ArrayList<String> likes) {
        ImageURL = imageURL;
        UuidUser = uuidUser;
        this.email = email;
        this.fullname = fullname;
        this.latitude = latitude;
        this.longitude = longitude;
        this.likes = likes;
        this.comments = new ArrayList<>();
    }

    public Post() {
        this.comments = new ArrayList<>();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void addComment(Comment comment) {
        if (this.comments.contains(comment)) return;
        this.comments.add(comment);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return ImageURL.equals(post.ImageURL)
                && UuidUser.equals(post.UuidUser)
                && email.equals(post.email)
                && fullname.equals(post.fullname)
                && latitude.equals(post.latitude)
                && longitude.equals(post.longitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ImageURL, UuidUser, email, fullname, latitude, longitude);
    }
}
