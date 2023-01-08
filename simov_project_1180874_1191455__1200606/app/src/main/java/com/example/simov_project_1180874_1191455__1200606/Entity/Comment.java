package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.Date;
import java.util.Objects;

public class Comment {
    private String text;
    private Date date;
    private String user;

    private Comment() {
    }

    public Comment(String text, Date date, String user) {
        this.text = text;
        this.date = date;
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public Date getDate() {
        return date;
    }

    public String getUser() {
        return user;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return text.equals(comment.text)
                && date.equals(comment.date)
                && user.equals(comment.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, date, user);
    }
}
