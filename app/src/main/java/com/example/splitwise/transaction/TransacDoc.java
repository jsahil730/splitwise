package com.example.splitwise.transaction;

import java.util.Date;

public class TransacDoc {

    private String groupID;
    private String description;
    private double totalAmount;
    private String tag;
    private Date date;

    public TransacDoc(){

    }

    public TransacDoc(String groupID, String description, double totalAmount, String tag, Date date) {
        this.groupID = groupID;
        this.description = description;
        this.totalAmount = totalAmount;
        this.tag = tag;
        this.date = date;
    }

    public String getGroupID() {
        return groupID;
    }

    public String getDescription() {
        return description;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getTag() {
        return tag;
    }

    public Date getDate() {
        return date;
    }
}
