package com.example.simov_project_1180874_1191455__1200606.Entity;

public class TempShowPost {
    private  String PostUrl;
    private String email;
    private String fullname;

    public TempShowPost(String postUrl, String email, String fullname) {
        PostUrl = postUrl;
        this.email = email;
        this.fullname = fullname;
    }

    public String getPostUrl() {
        return PostUrl;
    }

    public void setPostUrl(String postUrl) {
        PostUrl = postUrl;
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
}
