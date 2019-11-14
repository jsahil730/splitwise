package com.example.splitwise;

import com.google.firebase.firestore.Exclude;

public class FriendDoc {

    private String friendId;
    private String name;
    private double amount;

    public FriendDoc(){}

    public FriendDoc(String friendId, String name, double amount) {
        this.friendId = friendId;
        this.name = name;
        this.amount = amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Exclude
    public String getFriendId() {
        return friendId;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }


}
