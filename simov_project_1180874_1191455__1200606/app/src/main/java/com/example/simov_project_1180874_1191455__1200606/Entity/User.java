package com.example.simov_project_1180874_1191455__1200606.Entity;

public class User {
    public String fullname;
    public String email;
    public String phone;
    public String password;

    public User() {
    }

    public User(String fullname, String email, String phone, String password) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }



    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
