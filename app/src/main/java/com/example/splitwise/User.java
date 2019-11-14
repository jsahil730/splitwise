package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class User {

    private String name;
    private String userID;

    public User(){}

    public User(String name, String userID) {
        this.name = name;
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getUserID() {
        return userID;
    }
}
