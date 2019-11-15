package com.example.splitwise.transaction;

public class UserTransact {

    private String userID;
    private String name;
    private double amount_paid;
    private double stake;


    public UserTransact(){

    }

    public UserTransact(String userID, String name,double amount_paid, double stake) {
        this.userID = userID;
        this.name=name;
        this.amount_paid = amount_paid;
        this.stake = stake;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(double amount_paid) {
        this.amount_paid = amount_paid;
    }

    public double getStake() {
        return stake;
    }

    public void setStake(double stake) {
        this.stake = stake;
    }
}
