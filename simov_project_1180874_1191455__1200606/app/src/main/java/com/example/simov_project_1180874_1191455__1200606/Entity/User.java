package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.Objects;

public class User {
    public String fullname;
    public String email;
    public String phone;
    public String password;
    public FingerPrintStatusEnum fingerPrintStatusEnum;

    public User() {
    }

    public User(String fullname, String email, String phone, String password, FingerPrintStatusEnum fingerPrintStatusEnum) {
        this.fullname = fullname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.fingerPrintStatusEnum = fingerPrintStatusEnum;
    }

    public FingerPrintStatusEnum getFingerPrintStatusEnum() {
        return fingerPrintStatusEnum;
    }

    public void setFingerPrintStatusEnum(FingerPrintStatusEnum fingerPrintStatusEnum) {
        this.fingerPrintStatusEnum = fingerPrintStatusEnum;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return fullname.equals(user.fullname)
                && email.equals(user.email)
                && phone.equals(user.phone)
                && password.equals(user.password)
                && fingerPrintStatusEnum == user.fingerPrintStatusEnum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullname, email, phone, password, fingerPrintStatusEnum);
    }
}
