package com.example.simov_project_1180874_1191455__1200606.Entity;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final List<String> users;

    public Group() {
        users = new ArrayList<>();
    }

    public Group(String uuidUser) {
        users = new ArrayList<String>() {
            {
                add(uuidUser);
            }
        };
    }

    public int getNumberOfUsers() {
        return users.size();
    }

    public List<String> getUsers() {
        return users;
    }

    public void addUser(String uuidUser) {
        users.add(uuidUser);
    }
}
